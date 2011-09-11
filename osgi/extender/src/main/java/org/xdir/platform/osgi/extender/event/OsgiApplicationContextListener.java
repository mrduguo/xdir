package org.xdir.platform.osgi.extender.event;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextClosedEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextFailedEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextRefreshedEvent;


public class OsgiApplicationContextListener implements OsgiBundleApplicationContextListener<OsgiBundleApplicationContextEvent>
{
    private static final Logger logger = LoggerFactory.getLogger( OsgiApplicationContextListener.class );

    private OsgiApplicationContextTracker osgiApplicationContextTracker;


    public void onOsgiApplicationEvent( OsgiBundleApplicationContextEvent event )
    {
        if ( event instanceof OsgiBundleContextRefreshedEvent )
        {
            osgiApplicationContextTracker.onLazyStarted( event.getBundle().getBundleContext() );
            BundleUtils.debugBundleEvent( logger, event.getBundle().getBundleContext(), "application context started" );
        }
        else if ( event instanceof OsgiBundleContextFailedEvent )
        {
            OsgiBundleContextFailedEvent failedEvent = ( OsgiBundleContextFailedEvent ) event;
            osgiApplicationContextTracker.onLazyFailed( failedEvent.getBundle().getBundleContext(),failedEvent.getFailureCause());
        }
        else if ( event instanceof OsgiBundleContextClosedEvent )
        {
            BundleUtils.debugBundleEvent( logger, event.getBundle().getBundleContext(), "application context closed" );
        }
        else
        {
            BundleUtils.debugBundleEvent( logger, event.getBundle().getBundleContext(), "unknown event: "+event );
        }

    }


    public void setOsgiApplicationContextTracker( OsgiApplicationContextTracker osgiApplicationContextTracker )
    {
        this.osgiApplicationContextTracker = osgiApplicationContextTracker;
    }

}
