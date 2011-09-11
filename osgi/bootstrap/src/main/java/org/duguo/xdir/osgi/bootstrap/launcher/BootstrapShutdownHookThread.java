package org.duguo.xdir.osgi.bootstrap.launcher;

import org.duguo.xdir.osgi.bootstrap.i18n.Messages;
import org.duguo.xdir.osgi.bootstrap.log.Logger;


public class BootstrapShutdownHookThread extends AbstractBootstrapThread
{
    private static final String BOOTSTRAP_SHUTDOWNHOOK_NAME = "bootstrap-shutdownhook";
    
    public BootstrapShutdownHookThread(RuntimeContext runtimeContext){
        super( runtimeContext, BOOTSTRAP_SHUTDOWNHOOK_NAME );
    }

    @Override
    public synchronized void run()
    {
        Logger.debug( "shutdown hook triggered" );
        try
        {
            runtimeContext.getRuntimeLauncher().shutdownHookThread=null;
            if(!runtimeContext.getRuntimeLauncher().isStopped()){
                runtimeContext.getRuntimeLauncher().performStop();
                runtimeContext.getRuntimeLauncher().waitStopFinish();
            }
        }
        catch ( Throwable ex )
        {
            Logger.log( ex,Messages.ERROR_XDIR_RUNTIME_SHUTDOWN_HOOK_FAILED);
       }
        Logger.debug( "shutdown hook finished" );
    }
}
