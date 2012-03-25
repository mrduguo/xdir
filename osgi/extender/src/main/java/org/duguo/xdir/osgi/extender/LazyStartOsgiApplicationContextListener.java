package org.duguo.xdir.osgi.extender;


import org.eclipse.gemini.blueprint.context.event.*;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class LazyStartOsgiApplicationContextListener implements OsgiBundleApplicationContextListener<OsgiBundleApplicationContextEvent>
{
    private static final Logger logger = LoggerFactory.getLogger( LazyStartOsgiApplicationContextListener.class );

    private List<BundleListener> bundleListeners;


    public void onOsgiApplicationEvent( OsgiBundleApplicationContextEvent contextEvent )
    {
        if(logger.isDebugEnabled()) logger.debug("Received OSGi Extender contextEvent [{}] to bundle [{}]",contextEvent.getClass().getSimpleName(),contextEvent.getBundle());
        if ( contextEvent instanceof OsgiBundleContextRefreshedEvent )
        {
            sendLazyStartBundleEvent(contextEvent);
        }
        else if ( contextEvent instanceof OsgiBundleContextFailedEvent )
        {
            stopBundleOnFailure(contextEvent);
        }
    }

    private void sendLazyStartBundleEvent(OsgiBundleApplicationContextEvent contextEvent) {
        if ( bundleListeners!=null )
        {
            BundleEvent bundleEvent = new BundleEvent( BundleEvent.LAZY_ACTIVATION, contextEvent.getBundle() );
            for(BundleListener bundleListener:bundleListeners){
                bundleListener.bundleChanged( bundleEvent );
            }
            if(logger.isDebugEnabled()) logger.debug( "LAZY_ACTIVATION delivered to bootstrapBundleListener" );
        }
    }

    private void stopBundleOnFailure(OsgiBundleApplicationContextEvent contextEvent) {
        OsgiBundleContextFailedEvent failedEvent = ( OsgiBundleContextFailedEvent ) contextEvent;
        try{
            logger.debug("Failed to start extender bundle ["+contextEvent.getBundle()+"]",((OsgiBundleContextFailedEvent) contextEvent).getFailureCause());
            contextEvent.getBundle().stop();
        }catch (BundleException ex){
            throw new RuntimeException("Failed to stop bundle ["+contextEvent.getBundle()+"]",ex);
        }
    }


    public void setBundleListeners( List<BundleListener> bundleListeners )
    {
        this.bundleListeners = bundleListeners;
    }
}
