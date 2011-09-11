package org.xdir.platform.osgi.extender.event;


import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.extender.support.DefaultOsgiApplicationContextCreator;
import org.duguo.xdir.osgi.spi.ConsoleLogger;


public class TrackableOsgiApplicationContextCreator extends DefaultOsgiApplicationContextCreator implements
    BundleContextAware, InitializingBean
{
    private static final Logger logger = LoggerFactory.getLogger( TrackableOsgiApplicationContextCreator.class );

    // as the BlueprintLoaderListener will load this creator again, we need 
    // track the status to avoid send lazy activation event again
    private static boolean baseExtenderStarted = false;

    private OsgiApplicationContextTracker osgiApplicationContextTracker;
    private BundleContext bundleContext;
    private ConsoleLogger consoleLogger;

    public void afterPropertiesSet() throws Exception
    {
        if (!baseExtenderStarted )
        {
            baseExtenderStarted = true;
            osgiApplicationContextTracker.onLazyStarted( bundleContext );
            if(consoleLogger!=null){
            	consoleLogger.log("info.xdir.extender.started");
            }
        }
    }


    public DelegatedExecutionOsgiBundleApplicationContext createApplicationContext( BundleContext bundleContext )
        throws Exception
    {
        DelegatedExecutionOsgiBundleApplicationContext applicationContext = 
            super.createApplicationContext( bundleContext );
        if ( applicationContext == null )
        {
        	if(logger.isDebugEnabled())
        		logger.debug("deliver onLazyStarted event to none spring bundle");
            osgiApplicationContextTracker.onLazyStarted( bundleContext );
        }
        return applicationContext;
    }



    public void setOsgiApplicationContextTracker( OsgiApplicationContextTracker osgiApplicationContextTracker )
    {
        this.osgiApplicationContextTracker = osgiApplicationContextTracker;
    }


    public void setBundleContext( BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

	public ConsoleLogger getConsoleLogger() {
		return consoleLogger;
	}


	public void setConsoleLogger(ConsoleLogger consoleLogger) {
		this.consoleLogger = consoleLogger;
	}
}
