package com.ixxc.uiot.Utils;

import android.util.Log;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

public class Logger {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("Logger");

    public static void logException(Class<?> fromClass, Exception ex, String logMsg) {
        try {
            if (ex == null) {
                logger.log(Level.SEVERE, logMsg + " - " + fromClass.getSimpleName());
            } else {
                logger.log(Level.SEVERE, logMsg + " - " + fromClass.getSimpleName(), ex);
            }
            logger.info("-----------------------------------------------\n");
        } catch (Exception e) {
            System.out.println("Lỗi Logger.logException: " + e.getMessage());
        }
    }

    public static void logMsg(String logMsg) {
        try {
            Log.d("MY LOGGER", logMsg);
        } catch (Exception e) {
            System.out.println("Lỗi Logger.logException: " + e.getMessage());
        }
    }
}
