package org.duguo.xdir.osgi.bootstrap.osgiserver;


import org.duguo.xdir.osgi.bootstrap.conf.PropertiesUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.launch.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class AbstractOsgiServer {

    private static final Logger logger = LoggerFactory.getLogger(AbstractOsgiServer.class);

    protected Framework framework;
    protected BunldeEventListener bunldeEventListener;


    protected void runBundleGroups(File bundlesFolder) throws Exception {
        if (logger.isTraceEnabled()) logger.trace("> runBundleGroups", bundlesFolder.getAbsolutePath());

        scanFilesToRunInFolder(bundlesFolder);
        scanFoldersToRunInFolder(bundlesFolder);
        if (logger.isTraceEnabled()) logger.trace("< runBundleGroups", bundlesFolder.getAbsolutePath());
    }


    protected void waitBundleStartStatus(Bundle bundle, int status) throws Exception {
        long startupTimeout = System.currentTimeMillis()+PropertiesUtils.getLongValue("xdir.osgi.startup.timeout");
        while (bundle.getState() < status) {
            if (startupTimeout- System.currentTimeMillis()<0) {
                throw new RuntimeException("Wait for bundle status ["+status+"] timeout");
            }
            Thread.sleep(100);
        }
    }



    protected void runBundleFiles(List<String> bundleFiles) throws Exception {
        if (bundleFiles.size() > 0) {
            Collections.sort(bundleFiles);
            List<Bundle> bundleInstances = new ArrayList<Bundle>();
            for (String bundleName : bundleFiles) {
                File bundleFile = new File(bundleName);
                    // using reference: to avoid copy bundle files
                    Bundle installedBundle = framework.getBundleContext().installBundle(
                            "reference:" + bundleFile.toURI().toString());
                    if (installedBundle.getSymbolicName() == null) {
                        throw new RuntimeException("Invalid bundle jar file [" + bundleFile.getPath() + "], no Bundle-SymbolicName header found");
                    }
                    bundleInstances.add(installedBundle);
                    if (logger.isDebugEnabled())
                        logger.debug("Installed bundle file [" + bundleFile.getName() + "]");
            }

            startBundleInstances(bundleInstances);
        }
    }


    private void startBundleInstances(List<Bundle> bundleInstances) throws Exception {
        for (Bundle installedBundle : bundleInstances) {
            if (!BundleUtils.isFragment(installedBundle)) {
                installedBundle.start();
                if (logger.isDebugEnabled())
                    logger.debug("Starting bundle [" + installedBundle.getSymbolicName() + ":"
                            + installedBundle.getVersion().toString() + "]");
            }
        }

        long startupTimeout = System.currentTimeMillis()+PropertiesUtils.getLongValue("xdir.osgi.startup.timeout");
        while (bunldeEventListener.isStarting()) {
            if(bunldeEventListener.hasFailure()){
                Bundle currentBundle = framework.getBundleContext().getBundle(bunldeEventListener.getFailureBundles().iterator().next());
                throw new RuntimeException("Failed to start bundle [" + currentBundle
                        .getSymbolicName() + ":" + currentBundle.getVersion().toString() + ":" + BundleUtils
                        .parseBundleStatus(currentBundle.getState()) + "]");
            }else if (startupTimeout- System.currentTimeMillis()<0) {
                for (long bundleId : bunldeEventListener.getStartingQueue()) {
                    Bundle currentBundle = framework.getBundleContext().getBundle(bundleId);
                    logger.error("Failed bundle [" + currentBundle
                            .getSymbolicName() + ":" + currentBundle.getVersion().toString() + ":" + BundleUtils
                            .parseBundleStatus(currentBundle.getState()) + "]");
                }
                throw new RuntimeException("Wait for bundles start timeout");
            }
            Thread.sleep(100);
        }
        if (bunldeEventListener.hasFailure()) {
            for (long bundleId :bunldeEventListener.getFailureBundles()) {
                Bundle currentBundle = framework.getBundleContext().getBundle(bundleId);
                if (BundleUtils.isFragment(currentBundle)) {
                    logger.error("Failed to attach fragment bundle [" + currentBundle.getSymbolicName() + ":" + String.valueOf(currentBundle.getVersion().toString()) + "]");
                } else {
                    logger.error("Failed to start bundle [" + currentBundle.getSymbolicName() + ":" + String.valueOf(currentBundle.getVersion().toString()) + "]");
                }
            }
            throw new RuntimeException("Failed to start bundles");
        }
    }


    private void scanFoldersToRunInFolder(File scanFolder) throws Exception {
        List<String> foldersToRun = new ArrayList<String>();
        for (File fileToTest : scanFolder.listFiles()) {
            if (fileToTest.isDirectory()) {
                foldersToRun.add(fileToTest.getAbsolutePath());
            }
        }
        if (foldersToRun.size() > 0) {
            Collections.sort(foldersToRun);
            for (String folder : foldersToRun) {
                runBundleGroups(new File(folder));
            }
        }
    }


    private void scanFilesToRunInFolder(File scanFolder) throws Exception {
        List<String> bundlesToRun = new ArrayList<String>();
        for (File fileToTest : scanFolder.listFiles()) {
            String filePath = fileToTest.getPath();
            String fileName = fileToTest.getName();
            if (fileToTest.isFile() && fileName.endsWith(".jar")) {
                bundlesToRun.add(filePath);
            }
        }
        if (bundlesToRun.size() > 0) {
            runBundleFiles(bundlesToRun);
        }
    }
}
