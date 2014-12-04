package com.ullink.duration.logging;

import net.openhft.chronicle.ChronicleConfig;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.IndexedChronicle;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FastLogger
{
    private static final Logger           LOGGER                         = Logger.getLogger(FastLogger.class.getName());
    private static final int              EXCERPT_LINE_CAPACITY_IN_BYTES = 4096;
    private static final SimpleDateFormat LOG_DATETIME_FORMATTER         = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final SimpleDateFormat FILENAME_DATE_FORMATTER        = new SimpleDateFormat("yyyy-MM-dd");
    public static final  String           LOG_FOLDER_NAME                = "durations";
    public static final  String           LOGFILE_PREFIX                 = "duration-";

    private IndexedChronicle chronicle;
    private ExcerptAppender  appender;

    private static final ThreadLocal<FastLogger> context = new ThreadLocal<FastLogger>();

    private FastLogger()
    {
        try
        {
            // TODO: make path configurable, only use this as default
            final String logBaseDir = System.getProperty("java.io.tmpdir");
            final long currentTimeMillis = System.currentTimeMillis();
            final String fileName = LOGFILE_PREFIX + FILENAME_DATE_FORMATTER.format(currentTimeMillis) + "-" + getProcessIdAsString(currentTimeMillis)
                + "-" + Thread.currentThread().getId();

            String basePath = logBaseDir + File.separator + LOG_FOLDER_NAME + File.separator + fileName;
            //LOGGER.info("Base path is: " + basePath); // using standard java logging causes stackoverflow due to the hacky logger in EDMA
            ChronicleConfig config = ChronicleConfig.SMALL.clone();
            config.useUnsafe(true);
            chronicle = new IndexedChronicle(basePath, config);
            appender = chronicle.createAppender();
        }
        catch (IOException e)
        {
            LOGGER.log(Level.WARNING, "Error creating Chronicle appender: " + e);
        }
        finally
        {
            autoCloseChronicleOnExit();
        }
    }

    private void autoCloseChronicleOnExit()
    {
        Runtime currentRuntime = Runtime.getRuntime();
        try
        {
            currentRuntime.addShutdownHook(
                new Thread()
                {
                    @Override
                    public void run()
                    {
                        LOGGER.info("Shutting down appender");
                        try
                        {
                            chronicle.close();
                        }
                        catch (IOException e)
                        {
                            LOGGER.log(Level.WARNING, "Error closing chronicle appender: " + e);
                        }
                    }
                }
            );
        }
        catch (IllegalStateException e)
        {
            LOGGER.info("Could not close logger due to: " + e);
        }
    }

    public static FastLogger getInstance()
    {
        FastLogger instance = context.get();
        if (instance == null)
        {
            instance = new FastLogger();
            context.set(instance);
        }
        return instance;
    }

    public void log(final String logMessage)
    {
        appender.startExcerpt(EXCERPT_LINE_CAPACITY_IN_BYTES);
        appender.append(LOG_DATETIME_FORMATTER.format(System.currentTimeMillis()));
        appender.append(" ");
        appender.append(logMessage);
        appender.append('\n');
        appender.finish();
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
