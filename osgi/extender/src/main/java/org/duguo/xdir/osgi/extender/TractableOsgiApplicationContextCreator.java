package org.duguo.xdir.osgi.extender;


import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextRefreshedEvent;
import org.eclipse.gemini.blueprint.extender.support.DefaultOsgiApplicationContextCreator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class TractableOsgiApplicationContextCreator extends DefaultOsgiApplicationContextCreator implements
    BundleContextAware, InitializingBean, ApplicationContextAware
{
    private static final Logger logger = LoggerFactory.getLogger( TractableOsgiApplicationContextCreator.class );

    // as the BlueprintLoaderListener will load this creator again, we need 
    // track the status to avoid send lazy activation event again
    private static boolean baseExtenderStarted = false;

    private OsgiBundleApplicationContextListener osgiBundleApplicationContextListener;
    private ApplicationContext extenderApplicationContext;
    private BundleContext bundleContext;

    public void afterPropertiesSet() throws Exception
    {
        if (!baseExtenderStarted )
        {
            baseExtenderStarted = true;
            osgiBundleApplicationContextListener.onOsgiApplicationEvent(new OsgiBundleContextRefreshedEvent(extenderApplicationContext,bundleContext.getBundle()));
            if(logger.isDebugEnabled()) logger.debug("OSGi blueprint extender started");
        }
    }


    public DelegatedExecutionOsgiBundleApplicationContext createApplicationContext( BundleContext bundleContext )
        throws Exception
    {
        DelegatedExecutionOsgiBundleApplicationContext applicationContext =super.createApplicationContext( bundleContext );
        if ( applicationContext == null )
        {
        	if(logger.isDebugEnabled())
        		logger.debug("deliver onLazyStarted event to none spring bundle");
            osgiBundleApplicationContextListener.onOsgiApplicationEvent(new OsgiBundleContextRefreshedEvent(extenderApplicationContext,bundleContext.getBundle()));
        }
        return applicationContext;
    }

    public void setOsgiBundleApplicationContextListener(OsgiBundleApplicationContextListener osgiBundleApplicationContextListener) {
        this.osgiBundleApplicationContextListener = osgiBundleApplicationContextListener;
    }

    public void setBundleContext( BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

    public void setApplicationContext(ApplicationContext extenderApplicationContext) {
        this.extenderApplicationContext = extenderApplicationContext;
    }
}
