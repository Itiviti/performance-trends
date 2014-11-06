package com.ullink.logging;

import java.util.Date;

public class FastLogger {

    private static FastLogger instance;

    private FastLogger() {
    }

    public static FastLogger getInstance() {
        if (instance == null) {
            instance = new FastLogger();
        }
        return instance;
    }

    public void log(final String logMessage) {
        System.out.println(new Date() + " " + logMessage);
    }
}
