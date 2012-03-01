package org.duguo.xdir.osgi.extender.event;


import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OsgiApplicationContextTracker
{
    private static final Logger logger = LoggerFactory.getLogger( OsgiApplicationContextTracker.class );

    private List<BundleListener> bundleListeners;

    public synchronized void onLazyStarted( BundleContext bundleContext )
    {
        if ( bundleListeners!=null )
        {
            BundleEvent event = new BundleEvent( BundleEvent.LAZY_ACTIVATION, bundleContext.getBundle() );
            for(BundleListener bundleListener:bundleListeners){
                bundleListener.bundleChanged( event );   
            }
            if(logger.isDebugEnabled()) logger.debug( "LAZY_ACTIVATION delivered to bootstrapBundleListener" );
        }
        BundleUtils.debugBundleEvent( logger, bundleContext, "lazy started" );
    }


    public synchronized void onLazyFailed( BundleContext bundleContext, Throwable cause )
    {
        try
        {
            bundleContext.getBundle().stop();
        }
        catch ( BundleException e )
        {
            throw new RuntimeException( e );
        }
    }


    public void setBundleListeners( List<BundleListener> bundleListeners )
    {
        this.bundleListeners = bundleListeners;
    }
}
