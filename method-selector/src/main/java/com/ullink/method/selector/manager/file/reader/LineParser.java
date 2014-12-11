package com.ullink.method.selector.manager.file.reader;

import com.ullink.method.selector.model.FilterEntry;

abstract class LineParser
{
    abstract FilterEntry readLine(final String stringLine);
}
