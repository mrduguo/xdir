package org.duguo.xdir.osgi.bootstrap.provider;

import org.duguo.xdir.osgi.bootstrap.log.Logger;


public class EquinoxRuntimeProvider extends DefaultRuntimeProvider
{

    private static final String EQUINOX_CONSOLE_KEY = "osgi.console";
    private static final String EQUINOX_OSGI_STORAGE="org.osgi.framework.storage";
    

    protected void setupFrameworkSpecificConfiguration()
    {
        super.setupFrameworkSpecificConfiguration();
        if(Logger.isDebugEnabled())
            Logger.debug( "Setup equinox configuration ["+EQUINOX_OSGI_STORAGE+"] with value ["+runtimeContext.getConfiguration().retriveXdirOsgiBundlesCache()+"]" );
        runtimeContext.getConfiguration().putNewValueIfNotExist( EQUINOX_OSGI_STORAGE, runtimeContext.getConfiguration().retriveXdirOsgiBundlesCache() );
        if(runtimeContext.getConfiguration().isConsoleEnabled()){
            if(Logger.isDebugEnabled())
                Logger.debug( "Enable equinox console by add configuration ["+EQUINOX_CONSOLE_KEY+"] with value []" );
            runtimeContext.getConfiguration().put( EQUINOX_CONSOLE_KEY, "" );
        }
    }
    
}
