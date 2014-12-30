package com.ullink.duration.logging;

import net.openhft.chronicle.ChronicleConfig;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.IndexedChronicle;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;

public class FastLogger
{
    private static final int                     EXCERPT_LINE_CAPACITY_IN_BYTES = 4096;
    private static final SimpleDateFormat        FILENAME_DATE_FORMATTER        = new SimpleDateFormat("yyyy-MM-dd");
    private static final String                  JAVA_IO_TMPDIR_VM_ARG_KEY      = "java.io.tmpdir";
    private static final String                  LOG_FOLDER_NAME                = "durations";
    private static final String                  LOGFILE_PREFIX                 = "duration-";
    private static final ThreadLocal<FastLogger> THREAD_CONTEXT                 = new ThreadLocal<FastLogger>();

    private static String configuredLogBaseDir;

    private IndexedChronicle chronicle;
    private ExcerptAppender  appender;

    private FastLogger()
    {
        autoCloseChronicleOnExit();
        try
        {
            final long currentTimeMillis = System.currentTimeMillis();
            String basePath = createLogFileName(currentTimeMillis);
            ChronicleConfig config = ChronicleConfig.SMALL.clone();
            config.useUnsafe(true);
            chronicle = new IndexedChronicle(basePath, config);
            appender = chronicle.createAppender();
        }
        catch (IOException e)
        {
            System.err.println("Error creating Chronicle appender: " + e);
        }
    }

    public static void initLogPath(String logPath)
    {
        configuredLogBaseDir = logPath;
    }

    private String createLogFileName(long currentTimeMillis)
    {
        final String logBaseDir;
        if (configuredLogBaseDir != null)
        {
            logBaseDir = configuredLogBaseDir;
        }
        else
        {
            logBaseDir = System.getProperty(JAVA_IO_TMPDIR_VM_ARG_KEY);
        }

        // TODO: use a string formatter instead of the long string concats
        final String fileName =
            LOGFILE_PREFIX + FILENAME_DATE_FORMATTER.format(currentTimeMillis) + "-" + getProcessIdAsString(currentTimeMillis) + "-" + Thread.currentThread().getId();
        return logBaseDir + File.separator + LOG_FOLDER_NAME + File.separator + fileName;
    }

    private void autoCloseChronicleOnExit()
    {
        Runtime currentRuntime = Runtime.getRuntime();
        Thread shutdownHook = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    if (chronicle != null)
                    {
                        chronicle.close();
                    }
                }
                catch (IOException e)
                {
                    System.err.println("Error closing chronicle: " + e);
                }
            }
        };
        try
        {
            currentRuntime.addShutdownHook(shutdownHook);
        }
        catch (IllegalStateException e)
        {
            System.err.println("Error closing chronicle: " + e);
        }
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

    public static FastLogger getInstance()
    {
        FastLogger instance = THREAD_CONTEXT.get();
        if (instance == null)
        {
            instance = new FastLogger();
            THREAD_CONTEXT.set(instance);
        }
        return instance;
    }

    public void log(final String logMessage)
    {
        appender.startExcerpt(EXCERPT_LINE_CAPACITY_IN_BYTES);
        appender.append(logMessage);
        appender.append('\n');
        appender.finish();
    }
}