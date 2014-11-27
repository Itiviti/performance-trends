package com.ullink.duration.logging;

import net.openhft.chronicle.ChronicleConfig;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.IndexedChronicle;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

public class FastLogger
{
    private static final Logger           LOGGER                         = Logger.getLogger(FastLogger.class.getName());
    private static final int              EXCERPT_LINE_CAPACITY_IN_BYTES = 4096;
    private static final SimpleDateFormat LOG_DATETIME_FORMATTER         = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final SimpleDateFormat FILENAME_DATE_FORMATTER        = new SimpleDateFormat("yyyy-MM-dd");
    public static final  String           LOG_FOLDER_NAME                = "durations";
    public static final  String           LOGFILE_PREFIX                 = "duration-";
    private static FastLogger      instance;
    private        IndexedChronicle       chronicle;
    private        ExcerptAppender logger;

    private FastLogger()
    {
        try
        {
            // TODO: make path configurable, only use this as default
            final String logBaseDir = System.getProperty("java.io.tmpdir");
            final long currentTimeMillis = System.currentTimeMillis();
            final String fileName = LOGFILE_PREFIX + FILENAME_DATE_FORMATTER.format(currentTimeMillis) + "-" + getProcessIdAsString(currentTimeMillis);
            String basePath = logBaseDir + LOG_FOLDER_NAME + File.separator + fileName;
            LOGGER.info("Base path is: " + basePath);
            ChronicleConfig config = ChronicleConfig.LARGE.clone();
            config.useUnsafe(true);
            chronicle = new IndexedChronicle(basePath, config);
            logger = chronicle.createAppender();
        }
        catch (IOException e)
        {
            System.err.println("Error creating Chronicle logger: " + e);
        }

        closeChronicle();
    }

    private void closeChronicle()
    {
        Runtime currentRuntime = Runtime.getRuntime();
        currentRuntime.addShutdownHook(
            new Thread()
            {
                @Override
                public void run()
                {
                    LOGGER.info("Shutting down logger");
                    logger.close();
                }
            }
        );
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
        logger.startExcerpt(EXCERPT_LINE_CAPACITY_IN_BYTES);
        logger.append(LOG_DATETIME_FORMATTER.format(System.currentTimeMillis()));
        logger.append(" ");
        logger.append(logMessage);
        logger.append('\n');
        logger.finish();
    }

    private static String getProcessIdAsString(final long fallback)
    {
        /* something like '<pid>@<hostname>', at least in on Oracle JVMs */
        final String jvmProcessName = ManagementFactory.getRuntimeMXBean().getName();
        final int index = jvmProcessName.indexOf('@');
        if (index < 1)
        {
            return fallback + "";
        }
        else
        {
            return jvmProcessName.substring(0, index);
        }
    }
}
