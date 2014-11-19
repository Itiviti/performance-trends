package com.ullink.duration.logging;

import net.openhft.chronicle.logger.slf4j.ChronicleLoggingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastLogger
{
    static
    {
        if (System.getProperty(ChronicleLoggingConfig.KEY_PROPERTIES_FILE) == null)
        {
            System.setProperty(ChronicleLoggingConfig.KEY_PROPERTIES_FILE, "duration-logging.properties");
        }
    }

    private static Logger LOGGER = LoggerFactory.getLogger("DURATION");

    private static FastLogger instance;

    private FastLogger()
    {
    }

    public static FastLogger getInstance()
    {
        if (instance == null)
        {
            instance = new FastLogger();
        }
        return instance;
    }

    public void log(final String logMessage)
    {
        LOGGER.info(logMessage);
    }
}
