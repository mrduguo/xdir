package org.duguo.xdir.osgi.bootstrap.command;


import java.io.File;
import java.io.IOException;

import org.duguo.xdir.osgi.bootstrap.api.Command;
import org.duguo.xdir.osgi.bootstrap.conf.AbstractProperties;
import org.duguo.xdir.osgi.bootstrap.conf.OsgiProperties;
import org.duguo.xdir.osgi.bootstrap.i18n.Messages;
import org.duguo.xdir.osgi.bootstrap.log.Logger;


public abstract class AbstractCommand implements Command
{
    
    public static final String SERVER_START_LOCK_FILE = "serverstart.lock";
    public static final String SERVER_NEWCOMMAND_LOCK_FILE = "newcommand.lock";
    public static final String SERVER_RESTART_LOCK_FILE = "restart.lock";
    
    private OsgiProperties configuration;
    private long commandPollInterval=200; 
    private long commandTimeoutCount=3000; // 10 minutes = 10*60*1000/200
    private long commandPickupTimeoutCount=10;

    public void setupConfiguration(OsgiProperties configuration){
        this.configuration=configuration;
        setupPollAndTimeout( );
    }
    
    public long getCommandPollInterval()
    {        
        return commandPollInterval;
    }

    public long getCommandTimeoutCount()
    {
        return commandTimeoutCount;
    }
    
    public boolean isServerRunning( )
    {
        File serverStartFile = FileUtils.buildProcFile(configuration, SERVER_START_LOCK_FILE );
        if ( serverStartFile.exists() )
        {
            boolean isServerRunning=retriveServerRunningStatusWithCommand( );
            if(!isServerRunning){
                cleanServerLockFiles( );
            }
            return isServerRunning;
        }
        else
        {
            if(Logger.isDebugEnabled())
                Logger.debug( "Server is not running because server lock file doesn't exist ["+serverStartFile.getPath()+"]" );
            return false;
        }
    }



    private boolean retriveServerRunningStatusWithCommand()
    {
        return sendCommandRequest(  "status",System.currentTimeMillis() );
    }
    
    protected boolean sendCommandRequest( String command,long requestId)
    {
        File commandInputFile = FileUtils.buildProcFile(configuration,"command."+requestId+".input" );
        FileUtils.writeStringToFile( commandInputFile,command);
        if(Logger.isDebugEnabled())
            Logger.debug( "Sent command ["+command+"] by create file ["+commandInputFile.getPath()+"]" );
        
        try{
            createNewCommandFileIfNotExist();
            int i=0;
            while(commandInputFile.exists()){
                i++;
                if(i<commandPickupTimeoutCount){
                    Thread.sleep( getCommandPollInterval() );
                }else{
                    if(Logger.isDebugEnabled())
                        Logger.debug( "Server may not running, command request file still exist after "+(commandPickupTimeoutCount*getCommandPollInterval()/1000)+" seconds ["+commandInputFile.getPath()+"]" );
                    FileUtils.safelyDeleteFile( commandInputFile );
                    // send timeout, assume server is not running
                    return false;
                }
            }
        }catch(Exception ex){
            throw new RuntimeException("unknown exception",ex);
        }
        if(Logger.isDebugEnabled())
            Logger.debug( "The command request file has been deleted, assume request has been accepted" );
        //send get response from server
        return true;
    }

    protected void createNewCommandFileIfNotExist() throws IOException
    {
        File newCommandFile =retiveNewCommandFile();
        if (newCommandFile.exists() )
        {
            if(Logger.isDebugEnabled())
                Logger.debug( "New command trigger file ["+newCommandFile.getPath()+"] already exist and skip create the file" );
        }else{
            FileUtils.writeStringToFile( newCommandFile,"newcommand" );
            if(Logger.isDebugEnabled())
                Logger.debug( "Trigger server to pick up command request by create file ["+newCommandFile.getPath()+"]" );
        }
    }
    
    public File retiveNewCommandFile(){
        File newCommandFile = FileUtils.buildProcFile(configuration,SERVER_NEWCOMMAND_LOCK_FILE );
        return newCommandFile;
    }
    
    public void cleanServerLockFiles()
    {
        if(Logger.isDebugEnabled())
            Logger.debug( "Server is not running and will clear all lock files" );
    
        File serverStartFile = FileUtils.buildProcFile(configuration,SERVER_START_LOCK_FILE );
        File newCommandFile = FileUtils.buildProcFile(configuration,SERVER_NEWCOMMAND_LOCK_FILE );
        File serverRetartFile = FileUtils.buildProcFile(configuration,SERVER_RESTART_LOCK_FILE );
        
        FileUtils.safelyDeleteFile( serverStartFile );
        FileUtils.safelyDeleteFile( newCommandFile );
        FileUtils.safelyDeleteFile( serverRetartFile );
    }
    public OsgiProperties getConfiguration()
    {
        return configuration;
    }

    public void createRestartFile( )
    {
        File commandInputFile = FileUtils.buildProcFile(getConfiguration(),SERVER_RESTART_LOCK_FILE);
        FileUtils.writeStringToFile( commandInputFile,"restart");
        if(Logger.isDebugEnabled())
            Logger.debug( "Enable server restart by create file ["+commandInputFile.getPath()+"]" );
    }
    private void setupPollAndTimeout()
    {
        String confPoll=configuration.getXdirOsgiCmdPoll();
        if(confPoll==null){
            if(Logger.isDebugEnabled())
                Logger.debug( "Use default command poll value ["+commandPollInterval+"] milliseconds" );
            configuration.setXdirOsgiCmdPoll( String.valueOf( commandPollInterval ) );
        }else{
            try{
                commandPollInterval=Long.parseLong( confPoll );
                if(Logger.isDebugEnabled())
                    Logger.debug( "Parsed command poll value ["+commandPollInterval+"] milliseconds from configuration" );
            }catch(Exception ex){
                Logger.log(Messages.WARN_XDIR_CONF_INVALID_FORMAT, AbstractProperties.KEY_XDIR_OSGI_CMD_POLL, confPoll,String.valueOf( commandPollInterval ));
            }
        }
        
        String confTimeout=configuration.getXdirOsgiCmdTimeout();
        if(confTimeout==null){
            if(Logger.isDebugEnabled())
                Logger.debug( "Use default command timeout value ["+String.valueOf( commandTimeoutCount*commandPollInterval )+"] milliseconds" );
            configuration.setXdirOsgiCmdTimeout( String.valueOf( commandTimeoutCount*commandPollInterval ) );
        }else{
            try{
                commandTimeoutCount=Long.parseLong( confTimeout );
                commandTimeoutCount=commandTimeoutCount/commandPollInterval;
                if(Logger.isDebugEnabled())
                    Logger.debug( "Parsed command timeout value ["+String.valueOf( commandTimeoutCount*commandPollInterval )+"] milliseconds" );
           }catch(Exception ex){
                Logger.log(Messages.WARN_XDIR_CONF_INVALID_FORMAT, AbstractProperties.KEY_XDIR_OSGI_CMD_TIMEOUT, confTimeout,String.valueOf( commandTimeoutCount*commandPollInterval ));
            }
        }
    }

}
