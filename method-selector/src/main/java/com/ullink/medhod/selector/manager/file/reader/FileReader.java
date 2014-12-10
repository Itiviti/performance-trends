package com.ullink.medhod.selector.manager.file.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import com.ullink.medhod.selector.model.FilterEntry;

public interface FileReader
{
   Collection<FilterEntry> readFromFile(File file)  throws FileNotFoundException, IOException; 
}
