package org.duguo.xdir.osgi.bootstrap.launcher;


import org.duguo.xdir.osgi.bootstrap.i18n.Messages;
import org.duguo.xdir.osgi.bootstrap.log.Logger;


public class BootstrapStopperThread extends AbstractBootstrapThread
{
    private static final String BOOTSTRAP_STOPPER_NAME = "bootstrap-stopper";


    public BootstrapStopperThread( RuntimeContext runtimeContext )
    {
        super( runtimeContext, BOOTSTRAP_STOPPER_NAME );
    }


    @Override
    public synchronized void run()
    {
        try
        {
            if ( !runtimeContext.getRuntimeLauncher().isStopped() )
            {
                Logger.debug( "performing framework stop" );
                runtimeContext.getRuntimeProvider().stopFramework();
                runtimeContext.getRuntimeLauncher().waitStopFinish();
            }
        }
        catch ( Throwable ex )
        {
            Logger.log( ex );
            Logger.log( Messages.ERROR_XDIR_RUNTIME_STOP_FAILED );
        }
        Logger.debug( "perform framework stop finished" );
    }
}
