package net.geferon.economyplugin.database;

import com.google.common.io.ByteStreams;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class Database {
    //Main plugin;
    Logger logger;
    Connection connection;
    // The name of the table we created back in SQLite class.
    public String table = "gef_economy";
    public int tokens = 0;
    @Inject
    public Database(Logger instance){
        logger = instance;
    }

    public static class Data {
        public Data(UUID player, long balance, long lastEarn) {
            this.player = player;
            this.balance = balance;
            this.lastEarn = lastEarn;
        }
        public UUID player;
        public long balance;
        public long lastEarn;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize(){
        connection = getSQLConnection();
        try (PreparedStatement ps = connection.prepareStatement("SELECT 1"))
        {
            ResultSet rs = ps.executeQuery();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }

    private byte[] getUUIDBytes(UUID uuid) {
        byte[] bytes = new byte[16];
        ByteBuffer.wrap(bytes)
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits());
        return bytes;
    }

    private InputStream getUUID(UUID uuid) {
        byte[] bytes = new byte[16];
        ByteBuffer.wrap(bytes)
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits());
        return new ByteArrayInputStream(bytes);
    }
    private UUID convertBinaryStream(InputStream stream) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        try {
            buffer.put(ByteStreams.toByteArray(stream));
            buffer.flip();
            return new UUID(buffer.getLong(), buffer.getLong());
        } catch (IOException e) {
            // Handle the exception
        }
        return null;
    }

    public Data getUserData(UUID player) {
        try (
                Connection conn = getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE player = ?;");
            ) {
            //ps.setBinaryStream(1, getUUID(player));
            ps.setBytes(1, getUUIDBytes(player));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Data(player, rs.getLong("balance"), rs.getLong("lastEarn"));
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        }
        return new Data(player, 0L, 0L);
    }

    public void setUserData(Data data) {
        try (
                Connection conn = getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("REPLACE INTO " + table + " (player,balance,lastEarn) VALUES(?,?,?)");
            ) {
            //ps.setBinaryStream(1, getUUID(data.player));
            ps.setBytes(1, getUUIDBytes(data.player));

            ps.setLong(2, data.balance);
            ps.setLong(3, data.lastEarn);
            ps.executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        }
    }

    public void setFields(UUID player, Map<String, Object> fields) {
        var fieldsSet = fields.entrySet();

        String fieldsStr = fieldsSet.stream().map(f -> f.getKey()).collect(Collectors.joining(","));
        String valuesStr = String.join(",", Collections.nCopies(fieldsSet.size(), "?"));

        try (
                Connection conn = getSQLConnection();
                PreparedStatement ps = conn.prepareStatement("REPLACE INTO " + table + " (player,"+fieldsStr+") VALUES(?,"+valuesStr+")");
            ) {
            //ps.setBinaryStream(1, getUUID(player));
            ps.setBytes(1, getUUIDBytes(player));

            int i = 0;
            for (Map.Entry<String, Object> field : fieldsSet) {
                i++;

                ps.setObject(i + 1, field.getValue());
            }
            ps.executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        }
    }



    public long getBalance(UUID player) {
        var data = getUserData(player);
        if (data != null) return data.balance;
        return 0;
    }

    public void setBalance(UUID uuid, long balance) {
        setFields(uuid, new HashMap<>() {{put("balance", balance);}});
    }
}