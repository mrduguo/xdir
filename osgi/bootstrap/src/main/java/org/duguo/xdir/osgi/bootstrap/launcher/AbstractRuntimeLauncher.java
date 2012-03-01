package org.duguo.xdir.osgi.bootstrap.launcher;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.duguo.xdir.osgi.bootstrap.api.RuntimeProvider;
import org.duguo.xdir.osgi.bootstrap.command.FileUtils;
import org.duguo.xdir.osgi.bootstrap.i18n.Messages;

import org.duguo.xdir.osgi.bootstrap.provider.RuntimeProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AbstractRuntimeLauncher
{
    private static final Logger logger = LoggerFactory.getLogger(AbstractRuntimeLauncher.class);
    protected RuntimeContext runtimeContext;
    
    protected List<String> stopperRequests = new ArrayList<String>();
    protected Thread stopperThread;
    protected Thread shutdownHookThread;



    protected void createRuntimeProvider()
    {
        RuntimeProviderFactory runtimeProviderFactory=new RuntimeProviderFactory(runtimeContext);
        RuntimeProvider runtimeProvider=runtimeProviderFactory.createOsgiProvider();
        runtimeContext.setRuntimeProvider( runtimeProvider );
    }

    protected void performStart() throws Exception
    {
        BootstrapStarterThread starerThread = new BootstrapStarterThread(runtimeContext);
        starerThread.start();
    }

    public void performStop() throws Exception
    {
        logger.debug( "performing framework stop" );
        runtimeContext.getRuntimeProvider().stopFramework();
    }

    protected boolean isRunning()
    {
        return runtimeContext.getFramework().getState() == Bundle.ACTIVE;
    }

    protected boolean isStopped()
    {
        return runtimeContext.getFramework().getState() < Bundle.STOPPING;
    }

    protected boolean isStopping()
    {
        return runtimeContext.getFramework().getState() < Bundle.ACTIVE;
    }
}
