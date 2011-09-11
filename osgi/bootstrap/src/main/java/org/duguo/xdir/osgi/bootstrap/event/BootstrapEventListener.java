package org.duguo.xdir.osgi.bootstrap.event;


import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.launch.Framework;
import org.duguo.xdir.osgi.spi.ConsoleLogger;
import org.duguo.xdir.osgi.spi.conditional.ConditionalService;
import org.duguo.xdir.osgi.spi.util.ClassUtil;
import org.duguo.xdir.osgi.bootstrap.conf.OsgiProperties;
import org.duguo.xdir.osgi.bootstrap.i18n.Messages;
import org.duguo.xdir.osgi.bootstrap.launcher.RuntimeContext;
import org.duguo.xdir.osgi.bootstrap.log.ConsoleLoggerImpl;
import org.duguo.xdir.osgi.bootstrap.log.Logger;


public class BootstrapEventListener implements FrameworkListener
{

    public static final String EVENT_ON_PROPERTIES_LOADED = "on.properties.loaded";

    public static final String EVENT_ON_RUNTIME_CREATED = "on.runtime.created";
    public static final String EVENT_ON_RUNTIME_STARTED = "on.runtime.started";
    public static final String EVENT_ON_RUNTIME_STOPPED = "on.runtime.stopped";
    public static final String EVENT_ON_RUNTIME_FAILED = "on.runtime.failed";

    public static final String EVENT_ON_SYSTEM_BUNDLES_STARTED = "on.systembundles.started";
    public static final String EVENT_ON_SYSTEM_BUNDLES_FAILED = "on.systembundles.failed";
    public static final String EVENT_ON_USER_BUNDLES_STARTED = "on.userbundles.started";
    public static final String EVENT_ON_USER_BUNDLES_FAILED = "on.userbundles.failed";

    public static final String EVENT_ON_SERVER_STARTED = "on.server.started";

    private RuntimeContext runtimeContext;


    public BootstrapEventListener( RuntimeContext runtimeContext )
    {
        this.runtimeContext = runtimeContext;
    }


    /**
     * Make use of FrameworkEvent to handle:
     * FrameworkEvent.STARTED = restart server command
     * FrameworkEvent.STOPPED = stop server command
     * FrameworkEvent.ERROR   = log error message
     * FrameworkEvent.WARNING = log warn message
     * FrameworkEvent.INFO    = log info message
     * customer event         = event name without space
     * 
     */
    public void frameworkEvent( FrameworkEvent event )
    {
        if ( Logger.isDebugEnabled() )
            Logger.debug( "frameworkEvent(\"" + event.getType() + "\")" );

        if ( event.getType() == FrameworkEvent.INFO )
        {
            if ( event.getThrowable() != null )
            {
                String msg = event.getThrowable().getMessage();
                Logger.debug( "handle framework extension request [" + msg + "]" );
                if ( "restart".equals( msg ) )
                {
                    Logger.log( Messages.INFO_XDIR_RUNTIME_PERFORM_RESTART );
                    runtimeContext.getStartCommand().createRestartFile();
                    runtimeContext.getRuntimeLauncher().performStop();
                }
                else if ( "stop".equals( msg ) )
                {
                    runtimeContext.getRuntimeLauncher().performStop();
                }
                else if ( msg.startsWith( "install:" ) )
                {
                    String bundleListString=msg.substring( "install:".length() );
                    
                    performInstall( bundleListString );
                }
                else
                {
                    String logMsg = Messages.parseCombinedMessage( msg );
                    if ( logMsg != null )
                    {
                        Logger.log( logMsg );
                    }
                    else
                    {
                        onEvent( msg );
                    }
                }
            }
        }
    }


    protected synchronized void performInstall( String bundleListString )
    {
        System.clearProperty( "framework_install");
        runtimeContext.getBunldeEventListener().resetAlllQueue();
        List<String> bundleFiles=new ArrayList<String>();
        for(String bundleFile:bundleListString.split( "," )){
            bundleFiles.add( bundleFile );
        }
        try{
            runtimeContext.getRuntimeProvider().hotDeployBundles( bundleFiles );
            System.setProperty( "framework_install", "success" );
        }catch(BundleException ex){
            Logger.log( ex, Messages.ERROR_XDIR_BUNDLE_HOT_DEPLOY_FAILED,ex.getMessage() );
            System.setProperty( "framework_install", ex.getMessage() );
        }
    }


    public void onPropertiesLoaded()
    {
        onEvent( EVENT_ON_PROPERTIES_LOADED );
        if ( Logger.isDebugEnabled() )
        {
            StringBuilder propertiesSB = new StringBuilder( "Properties loaded" );

            OsgiProperties configuration = runtimeContext.getConfiguration();
            List<String> keys = new ArrayList<String>();
            for ( Object key : configuration.keySet() )
            {
                keys.add( ( String ) key );
            }
            Collections.sort( keys );
            for ( String key : keys )
            {
                propertiesSB.append( "\n" );
                propertiesSB.append( key );
                propertiesSB.append( "\t=" );
                propertiesSB.append( configuration.get( key ) );
            }
            Logger.debug( propertiesSB.toString() );
        }
        Logger.log( Messages.INFO_XDIR_RUNTIME_PERFORM_START );
    }


    public void onRuntimeCreated()
    {
        onEvent( EVENT_ON_RUNTIME_CREATED );
        if ( Logger.isDebugEnabled() )
            Logger.debug( "Runtime created" );
    }


    public void onRuntimeStarted()
    {
        onEvent( EVENT_ON_RUNTIME_STARTED );
        registerBootstrapServices();
        Logger.log( Messages.INFO_XDIR_EVENT_RUNTIME_STARTED );
    }


    public void onRuntimeStopped()
    {
        onEvent( EVENT_ON_RUNTIME_STOPPED );
        Logger.log( Messages.INFO_XDIR_EVENT_RUNTIME_STOPPED );
    }


    public void onRuntimeFailed()
    {
        onEvent( EVENT_ON_RUNTIME_FAILED );
    }


    public void onSystemBundlesStarted()
    {
        onEvent( EVENT_ON_SYSTEM_BUNDLES_STARTED );
        Logger.log( Messages.INFO_XDIR_EVENT_SYSTEM_BUNDLES_STARTED );
    }


    public void onSystemBundlesFailed()
    {
        onEvent( EVENT_ON_SYSTEM_BUNDLES_FAILED );
    }


    public void onUserBundlesStarted()
    {
        onEvent( EVENT_ON_USER_BUNDLES_STARTED );
        Logger.log( Messages.INFO_XDIR_EVENT_USER_BUNDLES_STARTED );
        onServerStarted();
    }


    public void onUserBundlesFailed()
    {
        onEvent( EVENT_ON_USER_BUNDLES_FAILED );
        onServerStarted();
    }


    public void onServerStarted()
    {
        onEvent( EVENT_ON_SERVER_STARTED );
        logServerInfo();
    }


    private void onEvent( String eventName )
    {
        if ( Logger.isDebugEnabled() )
            Logger.debug( "onEvent(\"" + eventName + "\")" );
        String hookClassName = runtimeContext.getConfiguration().getXdirOsgiCmdImpl( eventName );
        if ( hookClassName != null )
        {
            if ( Logger.isDebugEnabled() )
                Logger.debug( "Found event [" + eventName + "] hook class name [" + hookClassName + "]" );
            runtimeContext.getConfiguration().setXdirOsgiCmdImpl( eventName, hookClassName );
            try
            {
                Object eventHook = BootstrapEventListener.class.getClassLoader().loadClass( hookClassName )
                    .newInstance();
                Method eventMethod = eventHook.getClass().getMethod( "onEvent", new Class[]
                    { Framework.class, Map.class, String.class } );
                eventMethod.invoke( eventHook, new Object[]
                    { runtimeContext.getFramework(), runtimeContext.getConfiguration(), eventName } );
                if ( Logger.isDebugEnabled() )
                    Logger.debug( "Event [" + eventName + "] fired successfully" );
            }
            catch ( Throwable ex )
            {
                Logger.log( ex, Messages.ERROR_XDIR_EVENT_TRIGGER_FAILED, eventName );
            }
        }
    }


    private void logServerInfo()
    {
        int totalBundles = runtimeContext.getFramework().getBundleContext().getBundles().length;
        long uptimeValue=ManagementFactory.getRuntimeMXBean().getUptime();        
        Logger.log( Messages.INFO_XDIR_BUNDLES_SUMMARY, String.valueOf( totalBundles ), String.valueOf( uptimeValue/1000 ) );
    }


    private void registerBootstrapServices()
    {
        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put( "org.eclipse.gemini.blueprint.bean.name", "bootstrapFrameworkListener" );
        runtimeContext.getFramework().getBundleContext().registerService( 
            FrameworkListener.class.getName(), this,properties );
        Logger.debug( "Published bootstrapFrameworkListener as service" );

        properties = new Hashtable<String, String>();
        properties.put( "org.eclipse.gemini.blueprint.bean.name", "bootstrapBundleListener" );
        runtimeContext.getFramework().getBundleContext().registerService(
            BundleListener.class.getName(),runtimeContext.getBunldeEventListener(), properties );
        Logger.debug( "Published bootstrapBundleListener as service" );

        properties = new Hashtable<String, String>();
        properties.put( "org.eclipse.gemini.blueprint.bean.name", "conditionalService" );
        runtimeContext.getFramework().getBundleContext().registerService(
        		ConditionalService.class.getName(),runtimeContext.conditionalService, properties );
        Logger.debug( "Published conditionalService as service" );

        properties = new Hashtable<String, String>();
        ConsoleLogger consoleLogger=ClassUtil.loadRequiredInstanceFromSystemProperty(ConsoleLogger.class, ConsoleLoggerImpl.class, ConsoleLoggerImpl.KEY_XDIR_OSGI_CONSOLE_LOGGER_IMPL);
        properties.put( "org.eclipse.gemini.blueprint.bean.name", "consoleLogger" );
        runtimeContext.getFramework().getBundleContext().registerService(
        		ConsoleLogger.class.getName(),consoleLogger, properties );
        Logger.debug( "Published consoleLogger as service" );
        
        runtimeContext.getFramework().getBundleContext().addBundleListener(
            runtimeContext.getBunldeEventListener() );
        Logger.debug( "Registed bootstrapBundleListener to framework bundle" );

    }
}
