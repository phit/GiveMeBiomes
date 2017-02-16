package me.phit.gmb;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class Logging {
    public static void logTrace(String message, Object... params) {
        log(Level.TRACE, message, params);
    }

    public static void logDebug(String message, Object... params) {
        log(Level.DEBUG, message, params);
    }

    public static void logInfo(String message, Object... params) {
        log(Level.INFO, message, params);
    }

    public static void logWarning(String message, Object... params) {
        log(Level.WARN, message, params);
    }

    public static void logError(String message, Object... params) {
        log(Level.ERROR, message, params);
    }

    private static void log(Level logLevel, String message, Object... params) {
        LogManager.getLogger(GiveMeBiomes.MODID).log(logLevel, message, params);
    }
}
