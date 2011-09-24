package org.duguo.xdir.osgi.extender.conf;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class ConfFolderAwareScannerUtils
{

    private static final Logger logger = LoggerFactory.getLogger( ConfFolderAwareScannerUtils.class );

    private static final String KEY_XDIR_DIR_CONF = "xdir.dir.conf";
    private static final String KEY_XDIR_EXTENDER_CONF_PREFIX = "xdir.extender.prefix";

    private static final String VALUE_XDIR_EXTENDER_CONF_PREFIX = "local";
    protected static final String VALUE_XDIR_EXTENDER_CONF_XML = ".xml";
    protected static final String VALUE_XDIR_EXTENDER_CONF_PROP = ".properties";


    public static void scanLocalConfigurations( Bundle bundle, List<String> confFiles, String confSuffix )
    {
        String confBaseFolder = System.getProperty( KEY_XDIR_DIR_CONF );
        String confPrefix = System.getProperty( KEY_XDIR_EXTENDER_CONF_PREFIX, VALUE_XDIR_EXTENDER_CONF_PREFIX );
        
        // global configuration
        File baseFolder = new File( confBaseFolder );
        int initConfFileCount=confFiles.size();
        int globalConfFileCount=confFiles.size();
        addConfFilesInFolder( confFiles, confSuffix, confPrefix, baseFolder );
        globalConfFileCount=confFiles.size()-globalConfFileCount;

        // bundle symbolic name level configuration
        baseFolder = new File( baseFolder, bundle.getSymbolicName() );
        addConfFilesInFolder( confFiles, confSuffix, confPrefix, baseFolder );

        // bundle version level configuration
        baseFolder = new File( baseFolder, bundle.getVersion().toString() );
        addConfFilesInFolder( confFiles, confSuffix, confPrefix, baseFolder );

        // we don't add global configuration to confFiles unless there are bundle level configuration files
        if (confFiles.size() == globalConfFileCount)
        {
            confFiles.clear();
        }
        sortLocalFiles( confFiles, initConfFileCount );
    }


    private static void sortLocalFiles( List<String> confFiles, int initConfFileCount )
    {
        if ( confFiles.size() > initConfFileCount  )
        {
            List<String> localConfFiles=new ArrayList<String>();
            for(int i=initConfFileCount;i<confFiles.size();i++){
                localConfFiles.add( confFiles.get( i ) );
            }
            Collections.sort( localConfFiles );
            for(String localFile:localConfFiles){
                confFiles.remove( initConfFileCount );
                confFiles.add( localFile);
            }
        }
    }


    private static void addConfFilesInFolder( List<String> confFiles, String confSuffix, String confPrefix,
        File baseFolder )
    {
        if ( baseFolder.exists() && baseFolder.isDirectory() )
        {
            for ( File file : baseFolder.listFiles() )
            {
                if ( file.isFile() && file.getName().endsWith( confSuffix ) )
                {
                    if ( file.getName().startsWith( confPrefix ) )
                    {
                        String confFileUrl = file.toURI().toString();
                        confFiles.add( confFileUrl );
                        if ( logger.isDebugEnabled() )
                            logger.debug( "Configuration file found at local file system at [" + confFileUrl + "]" );
                    }
                    else
                    {
                        overrideFileIfFromBundle( confFiles, file );
                    }
                }
            }
        }
    }


    private static void overrideFileIfFromBundle( List<String> confFiles, File confFile )
    {
        String confFileName = confFile.getName();
        for ( int i = 0; i < confFiles.size(); i++ )
        {
            String bundleConfFileName = confFiles.get( i );
            if ( !bundleConfFileName.startsWith( "file" ) && bundleConfFileName.endsWith( "/" + confFileName ) )
            {
                String localConfFileUrl = confFile.toURI().toString();
                confFiles.remove( i );
                confFiles.add( i, localConfFileUrl );
                if ( logger.isDebugEnabled() )
                    logger.debug( "Override conf file [" + bundleConfFileName + "] with [" + localConfFileUrl + "]" );

            }
        }
    }

}
