package org.duguo.xdir.spi.util.io;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 
public class FileUtil
{
    
    private static final Logger logger = LoggerFactory.getLogger( FileUtil.class );


    public static final long ONE_KB = 1024;
    public static final long ONE_MB = ONE_KB * ONE_KB;
    public static final long ONE_GB = ONE_KB * ONE_MB;
    
    public static String readFileAsString( File sourceFile ) throws IOException
    {
        if ( sourceFile.exists() && sourceFile.isFile())
        {
            InputStream inputStream =new FileInputStream(sourceFile);
            return readStreamAsString(inputStream);
        }
        return null;
    }
    
    public static String getExtension( File sourceFile )
    {
        String fileName=sourceFile.getName();
        int splitIndex=fileName.lastIndexOf( "." );
        if(splitIndex>0){
            return fileName.substring( splitIndex ).toLowerCase();
        }
        return "";
    }
    
    public static String readStreamAsString( InputStream inputStream ) throws IOException
    {
        try {
            return IOUtils.toString(inputStream,"utf-8");
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }


    public static void writeStream(InputStream in, OutputStream out) throws IOException
    {     
        byte buffer[] = new byte[1024];
        int len=0;
        while (true)
        {
            len=in.read(buffer,0,1024);
            if (len<0 )
                break;
            out.write(buffer,0,len);
        }
    }  
    
    public static String byteCountToDisplaySize(long size) {
        String displaySize;
        if (size / ONE_GB > 0) {
            displaySize = size / ONE_GB + " GB";
            long mb=size % ONE_GB/ONE_MB;
            if(mb>0){
                displaySize +=" "+ String.valueOf(mb) + " MB";
            }
        } else if (size / ONE_MB > 0) {
            displaySize = String.valueOf(size / ONE_MB) + " MB";
            long kb=size % ONE_MB/ONE_KB;
            if(kb>0){
                displaySize +=" "+ String.valueOf(kb) + "KB";
            }
        } else if (size / ONE_KB > 0) {
            displaySize = String.valueOf(size / ONE_KB) + " KB";
            long bit=size % ONE_KB;
            if(bit>0){
                displaySize +=" "+ String.valueOf(bit) + " bytes";
            }
        } else {
            displaySize = String.valueOf(size) + " bytes";
        }
        return displaySize;
    }

    public static void zip(String folder, String targetFile) {
        try {
            File targetFolder=new File(targetFile).getParentFile();
            if(!targetFolder.exists()){
                targetFolder.mkdirs();
            }
            File folderFile = new File(folder);
            ZipOutputStream zipout = new ZipOutputStream(new FileOutputStream(targetFile));
            try {
                zipFile(zipout, folderFile.getAbsolutePath(), folderFile.getAbsoluteFile());
            } finally {
                zipout.close();
            }
        } catch (Exception ex) {
            logger.error("failed to zip folder:"+ex.getMessage(),ex);
        }
    }

    private static void zipFile(ZipOutputStream zipout, String folderbase, File targetFile) throws Exception {
        if(targetFile.isFile()){
            byte[] buf = new byte[1024];
            zipout.putNextEntry(new ZipEntry(targetFile.getAbsolutePath().substring(folderbase.length() + 1)));
    
            FileInputStream in = new FileInputStream(targetFile);
            try {
                int len;
                while ((len = in.read(buf)) > 0) {
                    zipout.write(buf, 0, len);
                }
            } finally {
                in.close();
            }
        }else{
            for(File file:targetFile.listFiles()){
                zipFile(zipout, folderbase, file);
            }
        }
    }
}
