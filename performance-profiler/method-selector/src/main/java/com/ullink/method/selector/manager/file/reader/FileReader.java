package com.ullink.method.selector.manager.file.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import com.ullink.method.selector.model.FilterEntry;

public interface FileReader
{
   Collection<FilterEntry> readFromFile(File file)  throws FileNotFoundException, IOException; 
}
