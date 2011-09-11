package org.duguo.xdir.osgi.bootstrap.command;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import org.duguo.xdir.osgi.bootstrap.conf.OsgiProperties;
import org.duguo.xdir.osgi.bootstrap.i18n.Messages;
import org.duguo.xdir.osgi.bootstrap.log.Logger;


public class FileUtils
{

    @SuppressWarnings("unchecked")
    public static File retriveJarFileContainsClass(Class targetClass) throws Exception
    {
        File jarFile = null;
        String className = targetClass.getSimpleName();
        String classFileName = className + ".class";
        String pathToMainClass = targetClass.getResource( classFileName ).toString();
        int mark = pathToMainClass.indexOf( "!" );
        String frameworkBundle = pathToMainClass.toString().substring( 4, mark );
        jarFile = new File( new URL( frameworkBundle ).getFile() );
        return jarFile;
    }
    
    public static File buildProcFile( OsgiProperties configuration, String filename )
    {
        return  new File(configuration.retriveXdirDirVar(), "proc/"+filename );
    }

    public static void safelyDeleteFile( File targetFile )
    {
        try
        {
            if ( targetFile.exists() )
            {
                targetFile.delete();
                if(Logger.isDebugEnabled())
                    Logger.debug( "Deleted file ["+targetFile.getPath()+"]" );
            }
        }
        catch ( Exception ignore )
        {
            // file may deleted by other place, works as design and ignore the exception
        }
    }


    public static void writeStringToFile( File targetFile, String singleText )
    {
        try{
            createFolderIfNotExist( targetFile.getParentFile() );
    
            FileWriter fstream = new FileWriter( targetFile, false );
            BufferedWriter out = new BufferedWriter( fstream );
            try
            {
                out.write( singleText );
            }
            finally
            {
                out.close();
            }           
        }catch(Throwable ex){
            throw new RuntimeException(Messages.format( Messages.ERROR_XDIR_FILE_WRITE_FAILED,targetFile.getPath()),ex);
        }
    }

    public static synchronized void printToFile( File targetFile, String textToAppend ) throws IOException
    {
        FileWriter fstream = new FileWriter( targetFile, true );
        BufferedWriter out = new BufferedWriter( fstream );
        try
        {
            out.write( textToAppend );
        }
        finally
        {
            out.close();
        }
    }


    public static String readFileFirstLineAsString( File sourceFile ) throws IOException
    {
        String textFromFile = null;
        if ( sourceFile.exists() && sourceFile.length() > 0 )
        {
            BufferedReader input = new BufferedReader( new FileReader( sourceFile ) );
            try
            {
                textFromFile = input.readLine();
            }
            finally
            {
                input.close();
            }
        }
        return textFromFile;
    }


    public static void verifyExistFolder( String folderToTest )
    {
        File folde=new File(folderToTest);
        if ( !folde.exists() )
        {
            throw new RuntimeException( Messages.format( Messages.ERROR_XDIR_FILE_REQIRED_FOLDER_NOT_EXIST,folderToTest));
        }
        else if ( !folde.isDirectory() )
        {
            throw new RuntimeException( Messages.format( Messages.ERROR_XDIR_FILE_FOLDER_IS_NOT_FOLDER,folderToTest));
        }
    }


    public static void verifyExistFile( String fileToTest )
    {
        File file=new File(fileToTest);
        if ( !file.exists() )
        {
            throw new RuntimeException( Messages.format( Messages.ERROR_XDIR_FILE_REQIRED_FILE_NOT_EXIST,fileToTest));
        }
        else if ( !file.isFile() )
        {
            throw new RuntimeException( Messages.format( Messages.ERROR_XDIR_FILE_REQIRED_IS_NOT_FILE,fileToTest));
        }
    }

    private static void createFolderIfNotExist( File folder )
    {
        if ( folder.exists() )
        {
            verifyExistFolder(folder.getPath());
            return;
        }
        else
        {
            folder.mkdirs();
        }
    }

    public static void deleteFile( File fileToDelete )
    {
        if(fileToDelete.isDirectory()){
            for(File child:fileToDelete.listFiles()){
                deleteFile(child);
            }
        }
        fileToDelete.delete();
    }
}
