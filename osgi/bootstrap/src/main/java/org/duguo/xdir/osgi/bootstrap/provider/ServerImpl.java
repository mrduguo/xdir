package org.duguo.xdir.osgi.bootstrap.provider;

import org.duguo.xdir.osgi.bootstrap.api.Server;
import org.duguo.xdir.osgi.bootstrap.conf.OsgiProperties;
import org.duguo.xdir.osgi.bootstrap.conf.OsgiPropertiesFactory;
import org.duguo.xdir.osgi.bootstrap.launcher.RuntimeLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerImpl implements Server
{
    private static final Logger logger = LoggerFactory.getLogger(ServerImpl.class);
    private RuntimeLauncher runtimeLauncher;

    public void start() throws Exception
    {
        OsgiPropertiesFactory configurationFactory = new OsgiPropertiesFactory();
        OsgiProperties configuration = configurationFactory.createOsgiProperties(  );
        if(logger.isInfoEnabled()) logger.info("Starting OSGi server...");
        runtimeLauncher = new RuntimeLauncher(configuration);
        runtimeLauncher.run();
        if(logger.isInfoEnabled()) logger.info("Started OSGi server");
    }

    public void stop() throws Exception
    {
        if(logger.isInfoEnabled()) logger.info("Stopping OSGi server...");
        runtimeLauncher.performStop();
        if(logger.isInfoEnabled()) logger.info("Stopped OSGi server");
    }
}
