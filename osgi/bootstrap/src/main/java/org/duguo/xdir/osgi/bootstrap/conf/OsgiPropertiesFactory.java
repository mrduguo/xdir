package org.duguo.xdir.osgi.bootstrap.conf;


import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.duguo.xdir.osgi.bootstrap.command.FileUtils;
import org.duguo.xdir.osgi.bootstrap.i18n.Messages;
import org.duguo.xdir.osgi.bootstrap.i18n.MessagesInitialiser;
import org.duguo.xdir.osgi.bootstrap.log.Logger;


/**
 * Factory to load OSGi configuration file from XDIR_DIR_HOME/osgi.properties by default.
 *  
 * @author Guo Du
 *
 */
public class OsgiPropertiesFactory
{

    public OsgiProperties createOsgiProperties( String... args )
    {
        setupDebugFlagFromArgs(args);
        OsgiProperties configuration = new OsgiProperties();
        verifyXdirDirHome( configuration );
        
        loadConfigFile( configuration );
        MessagesInitialiser.init( Messages.class,configuration.retriveXdirDirConf()+"/osgi.messages");
        
        setupDebugFlagFromConf( configuration );
        
        
        // fix for log4j
        setupLog4jProperties(configuration);
        
        return configuration;
    }



    private void setupDebugFlagFromConf( OsgiProperties configuration )
    {
        if(Logger.isDebugEnabled()){
            configuration.putXdirOsgiDebug( "true" );
        }else if(configuration.isDebugEnabled()){
            Logger.setDebugEnabled( true );
            Logger.debug( "Enabled debug from configuration file" );
        }
    }

    private void setupDebugFlagFromArgs( String[] args )
    {
        for(String arg:args){
            String cmd=arg.toLowerCase();
            if ( "-debug".equals( cmd ) )
            {
                Logger.setDebugEnabled( true );
                Logger.debug( "Parsed [-debug] from command line to enable debug" );
            }
        }
    }

    private void verifyXdirDirHome( OsgiProperties configuration )
    {
        String xdirHome = configuration.getXdirDirHome();
        if ( xdirHome == null )
        {
            try
            {
                File bootstrapJarFile = FileUtils.retriveJarFileContainsClass(this.getClass());
                xdirHome = bootstrapJarFile.getParentFile().getParentFile().getCanonicalPath();
                configuration.setXdirDirHome(xdirHome );
                if(Logger.isDebugEnabled())
                    Logger.debug( "Detected XDIR_DIR_HOME ["+xdirHome+"] from jar file ["+bootstrapJarFile+"]" );
            }
            catch ( Exception e )
            {
                throw new RuntimeException(Messages.ERROR_XDIR_FILE_FAIL_RESOLVE_XDIRHOME, e );
            }
        }else{
            if(Logger.isDebugEnabled())
                Logger.debug( "Retrived XDIR_DIR_HOME ["+xdirHome+"] from configuration" );
        }
        FileUtils.verifyExistFolder( xdirHome);
        configuration.retriveXdirDirData();
    }


    private void loadConfigFile( OsgiProperties configuration )
    {
        String configFileName =configuration.retriveXdirOsgiCmdConf();
        File configFile = new File( configFileName);
        if(configFile.exists() && configFile.isFile()){
            try
            {
                Properties newProperties=new Properties();
                FileInputStream fileInputStream = new FileInputStream( configFile);
                newProperties.load( fileInputStream );
                fileInputStream.close();
                             
                PropertiesUtils.replacePlaceHolders( configuration, newProperties );
                if(Logger.isDebugEnabled())
                    Logger.debug("OSGi configuration loaded from [{0}]",configFileName);
            }
            catch ( Exception e )
            {    
                throw new RuntimeException( Messages.format( Messages.ERROR_XDIR_CONF_LOAD_FILE_FAILED,configFileName),e);
            }
        }else{
            Logger.log( Messages.WARN_XDIR_CONF_FILE_NOT_FOUND, configFileName);
        }
    }
    
    private void setupLog4jProperties(OsgiProperties configuration ) {
        File log4jConfig=new File(configuration.retriveXdirDirConf(),"log4j.xml");
        if(!log4jConfig.exists()){
            log4jConfig=new File(configuration.retriveXdirDirConf(),"log4j.properties");
        }
        if(log4jConfig.exists()){
            if(Logger.isDebugEnabled())
                Logger.debug("Setup log4j configuration at [{0}]",log4jConfig.toURI().toString());
            System.setProperty("log4j.configuration",log4jConfig.toURI().toString());
        }
    }


}