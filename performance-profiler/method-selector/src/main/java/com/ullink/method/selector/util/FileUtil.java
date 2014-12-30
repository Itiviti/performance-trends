package com.ullink.method.selector.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;


public class FileUtil
{
    public static ArrayList<String> getLineList(final Reader reader) throws IOException
    {
        final BufferedReader bufferedReader = new BufferedReader(reader);
        final ArrayList<String> content = new ArrayList<String>();
        String fileLine = null;
        while ((fileLine = bufferedReader.readLine()) != null)
        {
            if (!StringUtil.isEmptyOrNull(fileLine))
            {
                content.add(fileLine);
            }
        }
        return content;
    }
    
    public static ArrayList<String> getLineList(final File file) throws FileNotFoundException, IOException
    {
        final FileReader fileReader = new FileReader(file);
        try
        {
            return getLineList(fileReader);
        }
        finally
        {
            fileReader.close();
        }
    }
}
