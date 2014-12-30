package com.ullink.method.selector.manager.file.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import com.ullink.method.selector.model.FilterEntry;
import com.ullink.method.selector.util.FileUtil;

class FileReaderAdapter implements FileReader
{
    private final LineParser lineParser;

    protected FileReaderAdapter(LineParser lineParser)
    {
        super();
        this.lineParser = lineParser;
    }

    @Override
    public final Collection<FilterEntry> readFromFile(File file) throws FileNotFoundException, IOException
    {
        final ArrayList<String> lines = FileUtil.getLineList(file);
        if (lines == null)
        {
            return null;
        }
        ArrayList<FilterEntry> entries = new ArrayList<FilterEntry>(lines.size());
        for (String line : lines)
        {
            entries.add(readLine(line));
        }
        return entries;
    }

    protected FilterEntry readLine(final String stringLine)
    {
        return this.lineParser.readLine(stringLine);
    }

}
