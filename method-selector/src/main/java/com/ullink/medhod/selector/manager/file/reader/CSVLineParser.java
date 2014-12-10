package com.ullink.medhod.selector.manager.file.reader;

import com.ullink.medhod.selector.model.FilterEntry;

final class CSVLineParser extends LineParser
{

    private static final String DEFAULT_SEPARATOR = ";";
    private final String        separator;

    public CSVLineParser()
    {
        this(DEFAULT_SEPARATOR);
    }

    public CSVLineParser(final String separator)
    {
        this.separator = separator;
    }

    @Override
    FilterEntry readLine(String stringLine)
    {
        String[] parts = stringLine.split(this.separator);
        if (parts.length < 4)
        {
            return null;
        }
        final String packageName = parts[0];
        final String className = parts[1];
        final String methodName = parts[2];
        final Boolean visible = Boolean.valueOf(parts[3]);
        return new FilterEntry(packageName, className, methodName, visible.booleanValue());
    }
}
