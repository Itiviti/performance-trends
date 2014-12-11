package com.ullink.performance.log.fomat;

/**
 * Created by laci on 08.12.2014.
 */
public class PerformanceTrendLogFormatter {
    public static final String LOG_SECTION_SEPARATOR = "`";
    private static final String STRING_PLACEHOLDER = "%s";
    public static final String LOG_MESSAGE_FORMAT = STRING_PLACEHOLDER + LOG_SECTION_SEPARATOR // package
                                                    + STRING_PLACEHOLDER + LOG_SECTION_SEPARATOR // class
                                                    + STRING_PLACEHOLDER + LOG_SECTION_SEPARATOR; // method
}
