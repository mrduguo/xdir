package org.duguo.xdir.osgi.bootstrap.provider;


import org.osgi.framework.BundleException;
import org.duguo.xdir.osgi.bootstrap.i18n.Messages;
import org.duguo.xdir.osgi.bootstrap.log.Logger;


public class FelixRuntimeProvider extends DefaultRuntimeProvider
{

    private static final String[] FELIX_CONSOLE_BUNDLES={"org.apache.felix.shell","org.apache.felix.shell.tui"};
    private static final String FELIX_DEBUG_FLAG="felix.log.level";
    private static final String FELIX_OSGI_STORAGE="org.osgi.framework.storage";
    
    protected void setupFrameworkSpecificConfiguration()
    {
        super.setupFrameworkSpecificConfiguration();
        if(Logger.isDebugEnabled())
            Logger.debug( "Setup felix configuration ["+FELIX_OSGI_STORAGE+"] with value ["+runtimeContext.getConfiguration().retriveXdirOsgiBundlesCache()+"]" );
        runtimeContext.getConfiguration().putNewValueIfNotExist( FELIX_OSGI_STORAGE, runtimeContext.getConfiguration().retriveXdirOsgiBundlesCache() );
        if(runtimeContext.getConfiguration().isDebugEnabled()){
            Logger.debug( "Setup felix configuration ["+FELIX_DEBUG_FLAG+"] with value [4]" );
            runtimeContext.getConfiguration().put( FELIX_DEBUG_FLAG, "4" );
        }        
    }

    public void startSystemBundles() throws BundleException
    {
        startConsoleBundlesIfNeeded();
        super.startSystemBundles();
    }
    
    private void startConsoleBundlesIfNeeded() throws BundleException
    {
        if(runtimeContext.getConfiguration().isConsoleEnabled()){
            if(Logger.isDebugEnabled())
                Logger.debug( "Starting felix console bundles" );
            int numberOfBundles=runBundlesIfExist(FELIX_CONSOLE_BUNDLES);
            if(numberOfBundles!=FELIX_CONSOLE_BUNDLES.length){
                Logger.log(Messages.WARN_XDIR_CONSOLE_BUNDLE_NOT_FOUND);
            }
            if(Logger.isDebugEnabled())
                Logger.debug( "Started felix console bundles ["+numberOfBundles+"]" );
        }
    }

}
