package org.duguo.xdir.osgi.bootstrap.command;

import java.io.File;

import org.duguo.xdir.osgi.bootstrap.api.Command;
import org.duguo.xdir.osgi.bootstrap.conf.OsgiProperties;
import org.duguo.xdir.osgi.bootstrap.i18n.Messages;
import org.duguo.xdir.osgi.bootstrap.launcher.RuntimeLauncher;
import org.duguo.xdir.osgi.bootstrap.log.Logger;


public class StartCommand extends AbstractCommand
{
    private RuntimeLauncher runtimeLauncher;

    public int execute(OsgiProperties configuration) throws Exception
    {
        setupConfiguration( configuration );
        
        int statusCode = 1;
        if ( !isServerRunning( ) )
        {   
            markServerAsRunningByCreateServerstartLockFile();
            
            try{                
                cleanVarFoldersIfRequired(configuration);
                Logger.init( configuration);
                
                runtimeLauncher = new RuntimeLauncher(this);
                statusCode = runtimeLauncher.run();
                
                if(hasRestartFile()){
                    if(statusCode==0){
                        statusCode=Command.RESTART_EXIT_CODE;
                    }else{
                        Logger.log( Messages.WARN_XDIR_MAIN_SERVER_IGNORE_RESTART,String.valueOf( statusCode));                        
                    }
                }
            }finally{
                cleanServerLockFiles();
            }
        }
        else
        {
            Logger.init( configuration);
            Logger.log( Messages.WARN_XDIR_MAIN_SERVER_RUNNING_IGNORE_START );
        }
        return statusCode;
    }

    private void cleanVarFoldersIfRequired(OsgiProperties configuration)
    {
        /*
         * The bundle cache folder must be clean before start, 
         * otherwise, the bundle event won't be captured. This 
         * will break the the application life cycle management.
         */
        File bundleCache=new File(configuration.retriveXdirOsgiBundlesCache());
        if(bundleCache.exists()){
            FileUtils.verifyExistFolder( bundleCache.getPath() );
            if(Logger.isDebugEnabled())
                Logger.debug( "Deleteing ["+bundleCache.getPath()+"]" );
            FileUtils.deleteFile(bundleCache);
            if(Logger.isDebugEnabled())
                Logger.debug( "Deleted ["+bundleCache.getPath()+"]" );
        }
        
        if(configuration.isCleanEnabled()){
            File varFolder=new File(configuration.retriveXdirDirVar());
            if(varFolder.exists()){
                FileUtils.verifyExistFolder( varFolder.getPath() );
                if(Logger.isDebugEnabled())
                    Logger.debug( "Delete file/folder in var folders except var/proc" );
                for(String fileName:varFolder.list()){
                    if("proc".equals( fileName )){
                        continue;
                    }
                    File fileToDelete=new File(varFolder,fileName);
                    if(Logger.isDebugEnabled())
                        Logger.debug( "Deleteing ["+fileToDelete.getPath()+"]" );
                    FileUtils.deleteFile(fileToDelete);
                    if("logs".equals( fileName )){
                        fileToDelete.mkdirs();
                    }
                    if(Logger.isDebugEnabled())
                        Logger.debug( "Deleted ["+fileToDelete.getPath()+"]" );  
                }
            }
        }
    }

    private void markServerAsRunningByCreateServerstartLockFile()
    {
        File serverStartFile = FileUtils.buildProcFile( getConfiguration(), SERVER_START_LOCK_FILE );
        FileUtils.writeStringToFile( serverStartFile, "running" );
        Thread.currentThread().setName( "bootstrap-main" );
        if(Logger.isDebugEnabled())
            Logger.debug( "Mark server as running by create lock file ["+serverStartFile.getPath()+"]" );
    }
    


    private boolean hasRestartFile( )
    {
        File commandInputFile = FileUtils.buildProcFile(getConfiguration(),SERVER_RESTART_LOCK_FILE );
        if(commandInputFile.exists()){
            if(Logger.isDebugEnabled())
                Logger.debug( "Server was flaged to restart with lock file ["+commandInputFile.getPath()+"]" );
            FileUtils.safelyDeleteFile( commandInputFile );
            return true;
        }
        return false;
    }

}
