package org.duguo.xdir.osgi.bootstrap.api;


import java.util.List;

import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.duguo.xdir.osgi.bootstrap.launcher.RuntimeContext;


public interface RuntimeProvider
{
    /**
     * to create the framework instance
     * 
     * @param factory OSGi FrameworkFactory
     */
    public void createFramework(RuntimeContext runtimeContext, FrameworkFactory factory );


    /**
     * start the framework and return with active status
     */
    public void startFramework() throws BundleException;


    /**
     * start system bundles at bundles/system/*
     */
    public void startBundles() throws BundleException;

    /**
     * hot deploy passed bundle files as a single group
     */
    public void hotDeployBundles(List<String> bundleFiles)throws BundleException;


    /**
     * stop the osgi framework and return when all bundle stopped
     */
    public void stopFramework() throws BundleException;
    
    
    /**
     * 
     * @return the framework instance
     */
    public Framework getFramework( );


}
