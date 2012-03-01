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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileUtils
{

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

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
}
