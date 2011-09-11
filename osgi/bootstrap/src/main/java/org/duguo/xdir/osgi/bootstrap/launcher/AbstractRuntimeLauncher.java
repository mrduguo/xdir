package org.duguo.xdir.osgi.bootstrap.launcher;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.duguo.xdir.osgi.bootstrap.api.RuntimeProvider;
import org.duguo.xdir.osgi.bootstrap.command.FileUtils;
import org.duguo.xdir.osgi.bootstrap.i18n.Messages;
import org.duguo.xdir.osgi.bootstrap.log.Logger;
import org.duguo.xdir.osgi.bootstrap.provider.RuntimeProviderFactory;



public class AbstractRuntimeLauncher 
{
    protected RuntimeContext runtimeContext;
    
    protected List<String> stopperRequests = new ArrayList<String>();
    protected Thread stopperThread;
    protected Thread shutdownHookThread;


    protected void waitRuntimeToStopping() throws Exception, InterruptedException
    {
        while (true)
        {   
            if(isStopping()){
                break;
            }
            runNewCommandIfExist();
            Thread.sleep( runtimeContext.getStartCommand().getCommandPollInterval() );
        }
    }

    protected void waitRuntimeToStop() throws Exception, InterruptedException
    {
        long poolInterval=runtimeContext.getStartCommand().getCommandPollInterval();
        long waitCount =runtimeContext.getStartCommand().getCommandTimeoutCount();
        while (!isStopped())
        {
            waitCount--;
            if ( waitCount <0 )
            {
                Logger.log(  Messages.ERROR_XDIR_RUNTIME_STOP_TIMEOUT);
                break;
            }
            runNewCommandIfExist();
            Thread.sleep(poolInterval);
        }
        runtimeContext.getRuntimeLauncher().runtimeStopped();
    }


    protected void createRuntimeProvider()
    {
        RuntimeProviderFactory runtimeProviderFactory=new RuntimeProviderFactory(runtimeContext);
        RuntimeProvider runtimeProvider=runtimeProviderFactory.createOsgiProvider();
        runtimeContext.setRuntimeProvider( runtimeProvider );
    }

    protected boolean performStart() throws Exception
    {
        BootstrapStarterThread starerThread = new BootstrapStarterThread(runtimeContext);
        starerThread.start();
        while ( starerThread.isStarting())
        {
            if(starerThread.isFailed()){
                break;
            }
            runNewCommandIfExist();
            Thread.sleep( runtimeContext.getStartCommand().getCommandPollInterval() );
        }
        return starerThread.isSuccess();
    }

    public void performStop()
    {
        if ( stopperThread == null )
        {
            Logger.log( Messages.INFO_XDIR_RUNTIME_PERFORM_STOP);
            stopperThread =new BootstrapStopperThread(runtimeContext);
            stopperThread.start();
        }
    }


    protected synchronized void runtimeStopped()
    {
        if(stopperRequests!=null){
            if(shutdownHookThread!=null)
            {
                Runtime.getRuntime().removeShutdownHook( shutdownHookThread );
                Logger.debug( "Bootstrap shutdown hook removed" );
            }
            for ( String resultFile : stopperRequests )
            {
                try
                {
                    FileUtils.writeStringToFile( new File( resultFile ), "stopped" );
                    if(Logger.isDebugEnabled())
                        Logger.debug( "Acknowledge the finish of command [stop] by create file ["+resultFile+"]" );
                }
                catch ( Throwable ex )
                {
                    Logger.log(ex);
                }
            }
            runtimeContext.getBootstrapEventListener().onRuntimeStopped();
            stopperRequests=null;
        }
    }
    private void runNewCommandIfExist() throws Exception
    {
        File newCommandFile = runtimeContext.getStartCommand().retiveNewCommandFile();
        if ( newCommandFile.exists() )
        {
            if(Logger.isDebugEnabled())
                Logger.debug( "Scan command files because new command file detected at ["+newCommandFile.getPath()+"]" );
            FileUtils.safelyDeleteFile( newCommandFile );
            for ( File fileToTest : newCommandFile.getParentFile().listFiles() )
            {
                if ( fileToTest.getName().startsWith( "command" ) )
                {
                
                    String commandStr = FileUtils.readFileFirstLineAsString( fileToTest );
                    if(Logger.isDebugEnabled())
                        Logger.debug( "Found command ["+commandStr+"] at ["+fileToTest.getPath()+"]" );
                    if ( commandStr.equals( "stop" ) &&  runtimeContext.getConfiguration().isStopCommandEnabled())
                    {
                        stopperRequests.add( fileToTest.getParent() + "/result." + fileToTest.getName() );
                        performStop(  );
                    }
                    if(Logger.isDebugEnabled())
                        Logger.debug( "Acknowledge the receive of command ["+commandStr+"] by delete ["+fileToTest.getPath()+"]" );
                    FileUtils.safelyDeleteFile( fileToTest );
                }
            }
        }
    }
    

    protected boolean isRunning()
    {
        return runtimeContext.getFramework().getState() == Bundle.ACTIVE;
    }

    protected boolean isStopped()
    {
        return runtimeContext.getFramework().getState() < Bundle.STOPPING;
    }

    protected boolean isStopping()
    {
        return runtimeContext.getFramework().getState() < Bundle.ACTIVE;
    }
}
