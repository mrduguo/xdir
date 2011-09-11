package org.duguo.xdir.osgi.bootstrap.launcher;

import org.osgi.framework.Bundle;
import org.osgi.framework.launch.Framework;
import org.duguo.xdir.osgi.spi.conditional.ConditionalService;
import org.duguo.xdir.osgi.bootstrap.api.RuntimeProvider;
import org.duguo.xdir.osgi.bootstrap.command.StartCommand;
import org.duguo.xdir.osgi.bootstrap.conf.OsgiProperties;
import org.duguo.xdir.osgi.bootstrap.event.BootstrapEventListener;
import org.duguo.xdir.osgi.bootstrap.event.BunldeEventListener;
import org.duguo.xdir.osgi.bootstrap.provider.FrameworkStoppedDuringStartupException;

public class RuntimeContext
{

    private OsgiProperties configuration;
    StartCommand startCommand;
    
    private BunldeEventListener bunldeEventListener;
    private BootstrapEventListener bootstrapEventListener;
    
    private RuntimeLauncher runtimeLauncher;
    private RuntimeProvider runtimeProvider;
    private Framework framework;
    public ConditionalService conditionalService;
    
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
    public void setStartCommand( StartCommand startCommand )
    {
        this.startCommand = startCommand;
    }
    public StartCommand getStartCommand()
    {
        return startCommand;
    }
    public BunldeEventListener getBunldeEventListener()
    {
        return bunldeEventListener;
    }
    public void setBunldeEventListener( BunldeEventListener bunldeEventListener )
    {
        this.bunldeEventListener = bunldeEventListener;
    }
    public BootstrapEventListener getBootstrapEventListener()
    {
        return bootstrapEventListener;
    }
    public void setBootstrapEventListener( BootstrapEventListener bootstrapEventListener )
    {
        this.bootstrapEventListener = bootstrapEventListener;
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
