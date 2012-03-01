package org.duguo.xdir.osgi.bootstrap.launcher;

import org.duguo.xdir.osgi.bootstrap.api.RuntimeProvider;
import org.duguo.xdir.osgi.bootstrap.conf.OsgiProperties;
import org.duguo.xdir.osgi.bootstrap.event.BunldeEventListener;
import org.duguo.xdir.osgi.bootstrap.provider.FrameworkStoppedDuringStartupException;
import org.osgi.framework.Bundle;
import org.osgi.framework.launch.Framework;

public class RuntimeContext
{

    private OsgiProperties configuration;
    
    private BunldeEventListener bunldeEventListener;
    
    private RuntimeLauncher runtimeLauncher;
    private RuntimeProvider runtimeProvider;
    private Framework framework;
    
    public void assertFrameworkRunning(){
        if(framework==null || framework.getState()!=Bundle.ACTIVE){
            throw new FrameworkStoppedDuringStartupException();
        }
    }
    
    public OsgiProperties getConfiguration()
    {
        return configuration;
    }
    public void setConfiguration( OsgiProperties configuration )
    {
        this.configuration = configuration;
    }
    public BunldeEventListener getBunldeEventListener()
    {
        return bunldeEventListener;
    }
    public void setBunldeEventListener( BunldeEventListener bunldeEventListener )
    {
        this.bunldeEventListener = bunldeEventListener;
    }
    public void setRuntimeLauncher( RuntimeLauncher runtimeLauncher )
    {
        this.runtimeLauncher = runtimeLauncher;
    }
    public RuntimeLauncher getRuntimeLauncher()
    {
        return runtimeLauncher;
    }
    public RuntimeProvider getRuntimeProvider()
    {
        return runtimeProvider;
    }
    public void setRuntimeProvider( RuntimeProvider runtimeProvider )
    {
        this.runtimeProvider = runtimeProvider;
    }
    public Framework getFramework()
    {
        return framework;
    }
    public void setFramework( Framework framework )
    {
        this.framework = framework;
    }
    
}
