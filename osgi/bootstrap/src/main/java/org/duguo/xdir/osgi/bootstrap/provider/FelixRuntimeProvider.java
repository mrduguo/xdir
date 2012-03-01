package org.duguo.xdir.osgi.bootstrap.provider;


import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


public class FelixRuntimeProvider extends DefaultRuntimeProvider
{

    private static final Logger logger = LoggerFactory.getLogger(FelixRuntimeProvider.class);
    private static final String FELIX_DEBUG_FLAG="felix.log.level";
    private static final String FELIX_OSGI_STORAGE="org.osgi.framework.storage";
    
    protected void setupFrameworkSpecificConfiguration()
    {
        super.setupFrameworkSpecificConfiguration();
        String bundleCacheFolder = runtimeContext.getConfiguration().retriveXdirOsgiBundlesCache();
        FileUtils.deleteQuietly(new File(bundleCacheFolder));
        if(logger.isDebugEnabled())
            logger.debug( "Setup felix configuration ["+FELIX_OSGI_STORAGE+"] with value ["+ bundleCacheFolder +"]" );
        runtimeContext.getConfiguration().putNewValueIfNotExist( FELIX_OSGI_STORAGE, bundleCacheFolder);
        if(logger.isDebugEnabled()){
            logger.debug( "Setup felix configuration ["+FELIX_DEBUG_FLAG+"] with value [4]" );
            runtimeContext.getConfiguration().put( FELIX_DEBUG_FLAG, "4" );
        }        
    }

}
