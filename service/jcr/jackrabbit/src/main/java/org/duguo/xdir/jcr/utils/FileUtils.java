package org.duguo.xdir.jcr.utils;


import java.io.File;
import java.io.IOException;


public class FileUtils
{

    /**
     * Simple version of copy directory. It will support override exist file 
     * flag compare with defualt FileUtils.copyDirectory 
     * 
     * @param srcDir
     * @param destDir
     * @param overrideExistFile
     * @throws IOException
     */
    public static void copyDirectory( File srcDir, File destDir, boolean overrideExistFile ) throws IOException
    {
        if ( !destDir.exists() )
        {
            if ( destDir.mkdirs() == false )
            {
                throw new IOException( "Destination '" + destDir + "' directory cannot be created" );
            }
        }
        // recurse
        File[] files = srcDir.listFiles();
        for ( int i = 0; i < files.length; i++ )
        {
            File copiedFile = new File( destDir, files[i].getName() );
            if ( files[i].isDirectory() )
            {
                copyDirectory( files[i], copiedFile, overrideExistFile );
            }
            else if ( overrideExistFile || !copiedFile.exists() )
            {
                org.apache.commons.io.FileUtils.copyFile( files[i], copiedFile );
            }
        }
    }
}
