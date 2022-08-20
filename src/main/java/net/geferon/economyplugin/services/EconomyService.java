package net.geferon.economyplugin.services;

import net.geferon.economyplugin.database.Database;
import net.geferon.economyplugin.util.BalanceException;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Random;

@Singleton
public class EconomyService {
    public static final long TIME_BETWEEN_EARN = 1 * 60 * 1000; // 1 minute, 60 seconds x min, 1000 ms x sec
    private static final long MIN_EARN = 1;
    private static final long MAX_EARN = 5;

    private final Database db;
    @Inject
    public EconomyService(Database db) {
        this.db = db;
    }

    public void pay(Player from, Player to, long amount) throws BalanceException {
        long fromBal = db.getBalance(from.getUniqueId());
        long toBal = db.getBalance(to.getUniqueId());

        // This should be covered by caller, but just in case
        if (amount <= 0) {
            throw new BalanceException("Invalid amount supplied");
        }

        // Not enough money, return false
        if (fromBal < amount) {
            throw new BalanceException("User doesn't have enough money to pay");
        }

        db.setBalance(from.getUniqueId(), fromBal - amount);
        db.setBalance(to.getUniqueId(), toBal + amount);
    }

    public long getBalance(Player player) {
        return db.getBalance(player.getUniqueId());
    }

    public void setBalance(Player player, long balance) {
        db.setBalance(player.getUniqueId(), balance);
    }

    public Long earn(Player player) throws BalanceException {
        var userData = db.getUserData(player.getUniqueId());
        Long curTime = System.currentTimeMillis();

        if ((userData.lastEarn + TIME_BETWEEN_EARN) >= curTime) {
            throw new BalanceException("You have already earned within the last minute");
        }

        Random rnd = new Random();
        var earned = rnd.nextLong(MIN_EARN, MAX_EARN + 1);
        userData.balance += earned;
        userData.lastEarn = System.currentTimeMillis();
        db.setUserData(userData);
        return earned;
    }
}
