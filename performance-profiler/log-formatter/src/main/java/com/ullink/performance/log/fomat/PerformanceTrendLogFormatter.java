package com.ullink.performance.log.fomat;

public class PerformanceTrendLogFormatter {
    public static final String ESCAPED_QUOTES       = "\"";

    private static final String LOG_SECTION_SEPARATOR = "`";
    private static final String STRING_PLACEHOLDER = "%s";
    private static final String LOG_MESSAGE_FORMAT = STRING_PLACEHOLDER + LOG_SECTION_SEPARATOR // startTimeMillis
                                                    + STRING_PLACEHOLDER + LOG_SECTION_SEPARATOR // package
                                                    + STRING_PLACEHOLDER + LOG_SECTION_SEPARATOR // class
                                                    + STRING_PLACEHOLDER + LOG_SECTION_SEPARATOR // method
                                                    + STRING_PLACEHOLDER + LOG_SECTION_SEPARATOR // threadName
                                                    + STRING_PLACEHOLDER + LOG_SECTION_SEPARATOR // duration
                                                    + STRING_PLACEHOLDER + LOG_SECTION_SEPARATOR // paramTypes
                                                    + STRING_PLACEHOLDER; // tag
    private static final String LOGGER_START         = "com.ullink.duration.logging.FastLogger.getInstance().log(";
    private static final String LOGGER_END           = ");";
    private static final String LOG_MESSAGE_TEMPLATE = ESCAPED_QUOTES + LOG_MESSAGE_FORMAT + ESCAPED_QUOTES;
    private static final String LOG_LINE = LOGGER_START + LOG_MESSAGE_TEMPLATE + LOGGER_END;

    public static String getLogLine(String startTimeMillis, String packageName, String className, String methodName, String threadName, String duration, String paramTypes, String tag)
    {
        return String.format(LOG_LINE, startTimeMillis, packageName, className, methodName, threadName, duration, paramTypes, tag);
    }
}
