package org.duguo.xdir.core.internal.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.duguo.xdir.util.io.FileUtil;

public class FileResource implements Resource
{
    
    private File dataFile;
    
    public FileResource(File dataFile){
        this.dataFile=dataFile;
    }

    public String getAsString() throws Exception
    {
        return FileUtil.readFileAsString( dataFile );
    }

    public InputStream getAsInputStream() throws Exception
    {
        return new FileInputStream( dataFile );
    }
}
