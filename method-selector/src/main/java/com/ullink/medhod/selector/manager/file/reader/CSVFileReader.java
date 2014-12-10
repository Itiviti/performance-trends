package com.ullink.medhod.selector.manager.file.reader;

public class CSVFileReader extends  FileReaderAdapter
{
    public CSVFileReader()
    {
        super(new CSVLineParser());
    }
    
    public CSVFileReader(final String separator)
    {
        super(new CSVLineParser(separator));
    } 
}
