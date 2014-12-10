package com.ullink.medhod.selector.manager.file.reader;

import com.ullink.medhod.selector.model.FilterEntry;

abstract class LineParser
{
    abstract FilterEntry readLine(final String stringLine);
}
