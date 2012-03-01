package org.duguo.xdir.osgi.bootstrap.launcher;


import org.duguo.xdir.osgi.bootstrap.conf.OsgiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RuntimeLauncher extends AbstractRuntimeLauncher
{
    private static final Logger logger = LoggerFactory.getLogger(RuntimeLauncher.class);
    public RuntimeLauncher( OsgiProperties osgiProperties )
    {
        runtimeContext=new RuntimeContext();
        runtimeContext.setConfiguration(osgiProperties);
        runtimeContext.setRuntimeLauncher( this );
    }

    public void run() throws Exception
    {
        createRuntimeProvider();
        performStart();
    }

}
