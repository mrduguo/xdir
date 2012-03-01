package org.duguo.xdir.osgi.bootstrap.launcher;

import org.duguo.xdir.osgi.bootstrap.i18n.Messages;

import org.duguo.xdir.osgi.bootstrap.provider.HandledException;
import org.duguo.xdir.osgi.bootstrap.provider.MessageHolderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BootstrapStarterThread
{
    protected RuntimeContext runtimeContext;
    private static final Logger logger = LoggerFactory.getLogger(BootstrapStarterThread.class);
    
    /**
     * 0  = STARTING
     * 1  = SUCCESS
     * -1 = FAILED
     */
    private int status=0;
    
    public BootstrapStarterThread(RuntimeContext runtimeContext){
        this.runtimeContext=runtimeContext;
    }
    
    public boolean isFailed(){
        return status==-1;
    }
    
    public boolean isSuccess(){
        return status==1;
    }
    
    public boolean isStarting(){
        return status==0;
    }

    public synchronized void start() throws Exception
    {
        startFramework();
        startBundles();
    }

    private void startBundles()throws Exception
    {
        if(logger.isTraceEnabled()) logger.trace("> startBundles");
        runtimeContext.getRuntimeProvider().startBundles();
        if(logger.isTraceEnabled()) logger.trace("< startBundles");
    }

    private void startFramework() throws Exception
    {
        if(logger.isTraceEnabled()) logger.trace("> startFramework");
        runtimeContext.getRuntimeProvider().startFramework();
        if(logger.isTraceEnabled()) logger.trace("< startFramework");
    }
}
