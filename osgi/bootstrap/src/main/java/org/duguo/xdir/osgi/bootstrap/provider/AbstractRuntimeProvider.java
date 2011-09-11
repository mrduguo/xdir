package org.duguo.xdir.osgi.bootstrap.provider;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.duguo.xdir.osgi.bootstrap.api.RuntimeProvider;
import org.duguo.xdir.osgi.bootstrap.event.BunldeEventListener;
import org.duguo.xdir.osgi.bootstrap.i18n.Messages;
import org.duguo.xdir.osgi.bootstrap.launcher.RuntimeContext;
import org.duguo.xdir.osgi.bootstrap.log.Logger;


public abstract class AbstractRuntimeProvider implements RuntimeProvider
{

    protected RuntimeContext runtimeContext;
    protected Framework framework;

    protected Map<String, String> installedBundleNameAndVersionCache;


    protected void initBundlesCache()
    {
        installedBundleNameAndVersionCache = new Hashtable<String, String>();
        putBundleInCache( framework );
    }


    protected int runBundlesIfExist( String[] bundleSymbolicNames ) throws BundleException
    {
        List<String> bundleFiles = new ArrayList<String>();
        for ( String bundleFileNameBase : bundleSymbolicNames )
        {
            BundleUtils.scanSingleBundle( bundleFileNameBase, new File( runtimeContext.getConfiguration()
                .retriveXdirOsgiBundlesBase() ), bundleFiles );
        }
        runBundleFiles( bundleFiles, false );
        return bundleFiles.size();
    }


    protected boolean runBundleGroups( String bundleGroups, boolean ignoreException ) throws BundleException
    {
        boolean isSuccess = true;
        if ( Logger.isDebugEnabled() )
            Logger.debug( "Run bundle groups [" + bundleGroups + "] with ignore exception flag ["
                + ( ignoreException ? "on" : "off" ) + "]" );
        File bundlesRootFolder = new File( runtimeContext.getConfiguration().retriveXdirOsgiBundlesBase() );
        Set<String> installedUserBundles = new HashSet<String>();
        for ( String groupBase : bundleGroups.split( "," ) )
        {
            if ( !runBundlesInFolder( bundlesRootFolder, groupBase, ignoreException, installedUserBundles ) )
            {
                isSuccess = false;
            }
        }
        if ( Logger.isDebugEnabled() )
            Logger.debug( "Run bundle groups [" + bundleGroups + "] finished with  [" + installedUserBundles.size()
                + "] bundles installed" );
        return isSuccess;
    }


    protected void waitBundleStartStatus( Bundle bundle, int status ) throws BundleException
    {
        try
        {
            long poolInterval = Long.parseLong( runtimeContext.getConfiguration().getXdirOsgiCmdPoll() );
            long waitCount = Long.parseLong( runtimeContext.getConfiguration().getXdirOsgiCmdTimeout() );
            waitCount = waitCount / poolInterval;
            while ( bundle.getState() < status )
            {
                waitCount--;
                if ( waitCount < 0 )
                {
                    throw new RuntimeException( Messages.format( Messages.ERROR_XDIR_RUNTIME_BUNDLE_START_TIMEOUT,
                        bundle.getSymbolicName(), bundle.getVersion().toString(), BundleUtils
                            .parseBundleStatus( status ), BundleUtils.parseBundleStatus( bundle.getState() ) ) );
                }
                Thread.sleep( poolInterval );
            }
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
    }


    protected void stopAndWaitForNoneActive( Bundle bundle ) throws BundleException
    {
        bundle.stop();
        try
        {
            long poolInterval = Long.parseLong( runtimeContext.getConfiguration().getXdirOsgiCmdPoll() );
            long waitCount = Long.parseLong( runtimeContext.getConfiguration().getXdirOsgiCmdTimeout() );
            waitCount = waitCount / poolInterval;
            while ( bundle.getState() == Bundle.ACTIVE )
            {
                waitCount--;
                if ( waitCount < 0 )
                {
                    throw new RuntimeException( Messages.format( Messages.ERROR_XDIR_RUNTIME_BUNDLE_START_TIMEOUT,
                        bundle.getSymbolicName(), bundle.getVersion().toString(), BundleUtils
                            .parseBundleStatus( Bundle.RESOLVED ), BundleUtils.parseBundleStatus( bundle.getState() ) ) );
                }
                Thread.sleep( poolInterval );
            }
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }

    }


    protected void runBundleFiles( List<String> bundleFiles, boolean ignoreException ) throws BundleException
    {
        if ( bundleFiles.size() > 0 )
        {
            Collections.sort( bundleFiles );
            List<Bundle> bundleInstances = new ArrayList<Bundle>();
            for ( String bundleName : bundleFiles )
            {
                File bundleFile = new File( bundleName );
                String bundleVersionAndName = BundleUtils.retriveJarBundleNameAndVersionId( bundleFile );
                if (!installedBundleNameAndVersionCache.containsKey( bundleVersionAndName ) )
                {
                    // using reference: to avoid copy bundle files
                    runtimeContext.assertFrameworkRunning();
                    Bundle installedBundle = getFramework().getBundleContext().installBundle(
                        "reference:"+bundleFile.toURI().toString() );
                    if(installedBundle.getSymbolicName()==null){
                        throw new MessageHolderException( Messages.format( Messages.ERROR_XDIR_BUNDLE_JAR_INVALID_JAR,bundleFile.getPath() ) );
                    }
                    bundleInstances.add( installedBundle );
                    putBundleInCache( installedBundle );
                    runtimeContext.getBunldeEventListener().processEvent( BunldeEventListener.EVENT_MANUAL_INSTALLED, installedBundle );
                    if ( Logger.isDebugEnabled() )
                        Logger.debug( "Installed bundle file [" + bundleFile.getName() + "]" );
                }
                else
                {
                    handleDuplicatedBundles( ignoreException, bundleFile, bundleVersionAndName );
                }
            }

            startBundleInstances( bundleInstances );
        }
    }


    private void startBundleInstances( List<Bundle> bundleInstances ) throws BundleException
    {
        for ( Bundle installedBundle : bundleInstances )
        {
            if ( !BundleUtils.isFragment( installedBundle ) )
            {
                installedBundle.start();
                if ( Logger.isDebugEnabled() )
                    Logger.debug( "Starting bundle [" + installedBundle.getSymbolicName() + ":"
                        + installedBundle.getVersion().toString() + "]" );
            }
        }

        try
        {
            long poolInterval = Long.parseLong( runtimeContext.getConfiguration().getXdirOsgiCmdPoll() );
            long waitCount = Long.parseLong( runtimeContext.getConfiguration().getXdirOsgiCmdTimeout() );
            waitCount = waitCount / poolInterval;
            while ( runtimeContext.getBunldeEventListener().isStarting())
            {
                runtimeContext.assertFrameworkRunning();
                waitCount--;
                if ( waitCount < 0 )
                {
                    for ( long bundleId : runtimeContext.getBunldeEventListener().getStartingQueue() )
                    {
                        Bundle currentBundle = runtimeContext.getFramework().getBundleContext().getBundle( bundleId );
                        if ( BundleUtils.isFragment( currentBundle ) )
                        {
                            Logger.log( Messages.ERROR_XDIR_RUNTIME_BUNDLE_START_TIMEOUT, currentBundle
                                .getSymbolicName(), currentBundle.getVersion().toString(), BundleUtils
                                .parseBundleStatus( currentBundle.getState() ) );
                        }
                        else
                        {
                            Logger.log( Messages.ERROR_XDIR_RUNTIME_BUNDLE_START_TIMEOUT, currentBundle
                                .getSymbolicName(), currentBundle.getVersion().toString(), BundleUtils
                                .parseBundleStatus( currentBundle.getState() ) );
                        }
                    }
                    throw new HandledException();
                }
                Thread.sleep( poolInterval );
            }
            if ( runtimeContext.getBunldeEventListener().hasFailure() )
            {
                for ( long bundleId : runtimeContext.getBunldeEventListener().getFailureBundles() )
                {
                    Bundle currentBundle = runtimeContext.getFramework().getBundleContext().getBundle( bundleId );
                    if ( BundleUtils.isFragment( currentBundle ) )
                    {
                        Logger.log( Messages.ERROR_XDIR_RUNTIME_FRAGMENT_FAILED, currentBundle.getSymbolicName(),
                            String.valueOf( currentBundle.getVersion().toString() ) );
                    }
                    else
                    {
                        Logger.log( Messages.ERROR_XDIR_RUNTIME_BUNDLE_FAILED, currentBundle.getSymbolicName(),
                            String.valueOf( currentBundle.getVersion().toString() ) );
                    }
                }
                throw new HandledException();
            }
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
    }


    private boolean runBundlesInFolder( File bundlesRootFolder, String baseFolder, boolean ignoreException,
        Set<String> installedUserBundles ) throws BundleException
    {
        boolean isSuccess = true;
        File scanFolder = new File( bundlesRootFolder, baseFolder );
        if ( scanFolder.exists() && scanFolder.isDirectory() )
        {
            if ( !scanFilesToRunInFolder( bundlesRootFolder, ignoreException, installedUserBundles, scanFolder ) )
            {
                isSuccess = false;
            }
            if ( !scanFoldersToRunInFolder( bundlesRootFolder, ignoreException, installedUserBundles, scanFolder ) )
            {
                isSuccess = false;
            }
        }
        return isSuccess;
    }


    private boolean scanFoldersToRunInFolder( File bundlesRootFolder, boolean ignoreException,
        Set<String> installedUserBundles, File scanFolder ) throws BundleException
    {
        boolean isSuccess = true;
        List<String> foldersToRun = new ArrayList<String>();
        for ( File fileToTest : scanFolder.listFiles() )
        {
            String filePath = fileToTest.getPath();
            if ( fileToTest.isDirectory() && !installedUserBundles.contains( filePath ) )
            {
            	String folderName=fileToTest.getName();
            	if(!runtimeContext.conditionalService.isConditionalString(folderName) ||
            		runtimeContext.conditionalService.eval(folderName)){
            		foldersToRun.add( filePath );
            	}
            }
        }
        if ( foldersToRun.size() > 0 )
        {
            Collections.sort( foldersToRun );
            for ( String folder : foldersToRun )
            {
                if ( !runBundlesInFolder( bundlesRootFolder, folder
                    .substring( bundlesRootFolder.getPath().length() + 1 ), ignoreException, installedUserBundles ) )
                {
                    isSuccess = false;
                }
            }
        }
        return isSuccess;
    }


    private boolean scanFilesToRunInFolder( File bundlesRootFolder, boolean ignoreException,
        Set<String> installedUserBundles, File scanFolder ) throws BundleException
    {
        boolean isSuccess = true;
        List<String> bundlesToRun = new ArrayList<String>();
        for ( File fileToTest : scanFolder.listFiles() )
        {
            String filePath = fileToTest.getPath();
            String fileName=fileToTest.getName();
            if ( fileToTest.isFile() && !installedUserBundles.contains( filePath ) ){
            	if(!runtimeContext.conditionalService.isConditionalString(fileName) ||
            		runtimeContext.conditionalService.eval(fileName.substring(0,fileName.lastIndexOf(".")))){
		            if (fileName.endsWith( ".jar" ))
		            {
		                    bundlesToRun.add( filePath );
		                    installedUserBundles.add( filePath );            		
	            	}
	            }
            }
        }
        if ( bundlesToRun.size() > 0 )
        {
            String groupName = scanFolder.getPath().substring( bundlesRootFolder.getPath().length() + 1 );
            if ( Logger.isDebugEnabled() )
                Logger.debug( "Starting bundle group [" + groupName + "]" );
            try
            {
                if ( ignoreException )
                {
                    try
                    {
                        isSuccess = false;
                        runBundleFiles( bundlesToRun, ignoreException );
                        isSuccess = true;
                    }
                    catch ( Throwable ex )
                    {
                        if ( ex instanceof HandledException )
                        {
                            
                        }
                        else if ( ex instanceof MessageHolderException )
                        {
                            Logger.log( ex.getMessage() );
                        }
                        else if ( Logger.hasMessageCode( ex.getMessage() ) )
                        {
                            Logger.log( ex );
                        }
                    }
                }
                else
                {
                    isSuccess = false;
                    runBundleFiles( bundlesToRun, ignoreException );
                    isSuccess = true;
                }
            }
            finally
            {
                if ( isSuccess )
                {
                    Logger.log( Messages.INFO_XDIR_BUNDLES_GROUP_STARTED, groupName, String.valueOf( bundlesToRun.size() ) );
                }
                else
                {
                    Logger.log( Messages.ERROR_XDIR_RUNTIME_GROUP_FAILED, groupName );
                }
            }
        }
        return isSuccess;
    }


    private void handleDuplicatedBundles( boolean ignoreException, File bundleFile, String bundleVersionAndName )
    {
        String newBundleUri = bundleFile.toURI().toString();
        String existBundleUri = installedBundleNameAndVersionCache.get( bundleVersionAndName );
        if ( !newBundleUri.equals( existBundleUri ) )
        {
            if ( ignoreException )
            {
                Logger.log( Messages.WARN_XDIR_BUNDLE_DUPLICATED_BUNDLE, bundleVersionAndName, existBundleUri,
                    newBundleUri );
            }
            else
            {
                String msg = Messages.format( Messages.ERROR_XDIR_BUNDLE_DUPLICATED_BUNDLE, bundleVersionAndName,
                    existBundleUri, newBundleUri );
                throw new RuntimeException( msg );
            }
        }
        else
        {
            if ( Logger.isDebugEnabled() )
                Logger.debug( "Bundle file [" + newBundleUri + "] already started previously" );
        }
    }


    private void putBundleInCache( Bundle bundle )
    {
        StringBuilder key = new StringBuilder();
        key.append( bundle.getSymbolicName() );
        key.append( ":" );
        key.append( bundle.getVersion().toString() );
        installedBundleNameAndVersionCache.put( key.toString(), bundle.getLocation() );
    }
}
