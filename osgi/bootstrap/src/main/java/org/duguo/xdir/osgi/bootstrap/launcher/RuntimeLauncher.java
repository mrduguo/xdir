package org.duguo.xdir.osgi.bootstrap.launcher;


import org.duguo.xdir.osgi.bootstrap.command.StartCommand;
import org.duguo.xdir.osgi.bootstrap.event.BootstrapEventListener;
import org.duguo.xdir.osgi.bootstrap.i18n.Messages;
import org.duguo.xdir.osgi.bootstrap.log.Logger;



public class RuntimeLauncher extends AbstractRuntimeLauncher
{
    public RuntimeLauncher( StartCommand startCommand )
    {
        runtimeContext=new RuntimeContext();
        runtimeContext.setConfiguration( startCommand.getConfiguration() );
        runtimeContext.setStartCommand( startCommand );
        runtimeContext.setRuntimeLauncher( this );
    }

    public int run()
    {
        int statusCode=1;
        try{
            BootstrapEventListener bootstrapEventListener=new BootstrapEventListener(runtimeContext);
            runtimeContext.setBootstrapEventListener( bootstrapEventListener );
            bootstrapEventListener.onPropertiesLoaded();
            
            createRuntimeProvider();
            setupLockAndShutdownHook();
            bootstrapEventListener.onRuntimeCreated();
            
            if(performStart()){
                waitRuntimeToStopping();
                statusCode=0;
            }else if(isRunning()){
                if(runtimeContext.getConfiguration().isConsoleEnabled()){
                    Logger.log( Messages.WARN_XDIR_RUNTIME_FAILED_WITH_CONSOLE);
                    waitRuntimeToStopping();
                }else{
                    performStop();
                }
            }
            waitRuntimeToStop();
            waitStopFinish();
        }catch(Throwable ex){
            Logger.log(ex);
            Logger.log(Messages.ERROR_XDIR_RUNTIME_RUN_FAILED);
        }
        return statusCode;
    }

    protected void waitStopFinish() throws Exception, InterruptedException
    {
        while (stopperRequests!=null)
        {
            Thread.sleep( runtimeContext.getStartCommand().getCommandPollInterval() );
        }
    }


    private void setupLockAndShutdownHook() throws Exception
    {
        shutdownHookThread = new BootstrapShutdownHookThread( runtimeContext );
        Runtime.getRuntime().addShutdownHook( shutdownHookThread );
        if(Logger.isDebugEnabled())
            Logger.debug( "Bootstrap shutdown hook registered" );
    }
}
