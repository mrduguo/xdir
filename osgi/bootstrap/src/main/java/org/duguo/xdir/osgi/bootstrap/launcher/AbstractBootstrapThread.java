package org.duguo.xdir.osgi.bootstrap.launcher;



public abstract class AbstractBootstrapThread extends Thread
{
    protected RuntimeContext runtimeContext;
    
    public AbstractBootstrapThread(RuntimeContext runtimeContext,String threadName){
        this.runtimeContext=runtimeContext;
        setName(threadName);
    }
}
