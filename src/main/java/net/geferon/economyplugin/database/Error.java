package net.geferon.economyplugin.database;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Error {
    public static void execute(Logger logger, Exception ex){
        logger.log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }
    public static void close(Logger logger, Exception ex){
        logger.log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}