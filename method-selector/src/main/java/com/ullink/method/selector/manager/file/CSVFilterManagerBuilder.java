package com.ullink.method.selector.manager.file;

import java.io.File;
import com.ullink.method.selector.manager.file.reader.CSVFileReader;

public class CSVFilterManagerBuilder extends BaseFileFilterManagerBuilder
{
    public static CSVFilterManagerBuilder fromFile(final File file)
    {
        return new CSVFilterManagerBuilder(file);
    }

    public static CSVFilterManagerBuilder fromFileName(final String fileName)
    {
        return new CSVFilterManagerBuilder(new File(fileName));
    }
    
    private CSVFilterManagerBuilder(File file)
    {
        super(file, new CSVFileReader());
    }

}
