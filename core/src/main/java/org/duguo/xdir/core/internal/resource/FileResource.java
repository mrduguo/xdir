package org.duguo.xdir.core.internal.resource;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FileResource implements Resource
{
    
    private File dataFile;
    
    public FileResource(File dataFile){
        this.dataFile=dataFile;
    }

    public String getAsString() throws Exception
    {
        return FileUtils.readFileToString(dataFile, "utf-8");
    }

    public InputStream getAsInputStream() throws Exception
    {
        return new FileInputStream( dataFile );
    }
}
