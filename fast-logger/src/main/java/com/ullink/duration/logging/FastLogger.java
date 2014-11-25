package com.ullink.duration.logging;

import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.IndexedChronicle;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class FastLogger
{

    private static final int              EXCERPT_LINE_CAPACITY_IN_BYTES = 512;
    private static final SimpleDateFormat LOG_DATETIME_FORMATTER         = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss.SSS");
    private static final SimpleDateFormat FILENAME_DATE_FORMATTER        = new SimpleDateFormat("yyyy-MM-DD");
    public static final  String           LOG_FOLDER_NAME                = "durations";
    public static final String LOGFILE_PREFIX = "duration-";
    private static FastLogger      instance;
    private        Chronicle       chronicle;
    private        ExcerptAppender logger;

    private FastLogger()
    {
        try
        {
            // TODO: make path configurable, only use this as default
            final String logBaseDir = System.getProperty("java.io.tmpdir");
            final String fileName = LOGFILE_PREFIX + FILENAME_DATE_FORMATTER.format(System.currentTimeMillis());
            String basePath = logBaseDir + LOG_FOLDER_NAME + File.separator + fileName;
            System.out.println("Base path is: " + basePath);
            chronicle = new IndexedChronicle(basePath);
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
                    System.out.println("Shutting down logger");
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
}
