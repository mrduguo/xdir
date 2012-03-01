package org.duguo.xdir.osgi.bootstrap.provider;




import java.io.File;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.duguo.xdir.osgi.bootstrap.event.BunldeEventListener;
import org.duguo.xdir.osgi.bootstrap.launcher.RuntimeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DefaultRuntimeProvider extends AbstractRuntimeProvider
{
    private static final Logger logger = LoggerFactory.getLogger(DefaultRuntimeProvider.class);
	

    public static final String KEY_EXTRA_SYSTEM_PACKAGES="org.osgi.framework.system.packages.extra";

    public void createFramework(RuntimeContext runtimeContext, FrameworkFactory factory )
    {
        this.runtimeContext=runtimeContext;
        
        setupExtraSystemPackages();
        
        setupFrameworkSpecificConfiguration();

        logger.debug( "Creating OSGi framework" );
        framework = factory.newFramework( runtimeContext.getConfiguration());
        runtimeContext.setFramework( framework );
        initBundlesCache();
        logger.debug( "Created OSGi framework" );
    }

    protected void setupExtraSystemPackages() {

        if(!runtimeContext.getConfiguration().containsKey(KEY_EXTRA_SYSTEM_PACKAGES)){
        	StringBuilder extraSystemPackages=new StringBuilder();
        	
        	setupExtraSystemPackagesScanBootJars(extraSystemPackages);
        	
        	setupExtraSystemPackagesScanExtendedKeys(extraSystemPackages);
        	
            if(logger.isDebugEnabled())
                logger.debug( "Setup  ["+KEY_EXTRA_SYSTEM_PACKAGES+"] with value ["+extraSystemPackages+"]" );
            runtimeContext.getConfiguration().put( KEY_EXTRA_SYSTEM_PACKAGES, extraSystemPackages.toString() );
        }else{
        	if(logger.isDebugEnabled())
                logger.debug( "User already provided  ["+KEY_EXTRA_SYSTEM_PACKAGES+"] with value ["+runtimeContext.getConfiguration().get(KEY_EXTRA_SYSTEM_PACKAGES)+"]" );
        }
		
	}

	private void setupExtraSystemPackagesScanBootJars(
			StringBuilder extraSystemPackages) {
		String[] jarFileNames = System.getProperty("java.class.path").split(System.getProperty("path.separator"));
		for(String jarFileName:jarFileNames){
			File jarFile=new File(jarFileName);
			if(jarFile.isFile()){
				try{
					JarFile jar = new JarFile(jarFile);
		        	Manifest mf = jar.getManifest();
		        	String exportPackage = mf.getMainAttributes().getValue(Constants.EXPORT_PACKAGE);
		        	if(exportPackage!=null){
		    			if(extraSystemPackages.length()>0){
		    				extraSystemPackages.append(",");
		    			}
		    			extraSystemPackages.append(exportPackage);
		        	}
				}catch(Exception ex){
					ex.printStackTrace();
				}	
			}
		}
	}

	/**
	 * 	examples:
	 *	org.osgi.framework.system.packages.extra.1
	 *	org.osgi.framework.system.packages.extra.foo.bar
	 */
	private void setupExtraSystemPackagesScanExtendedKeys(
			StringBuilder extraSystemPackages) {
		String extraKeys=KEY_EXTRA_SYSTEM_PACKAGES+".";
		for(Object keyObject:runtimeContext.getConfiguration().keySet()){
			String key=(String)keyObject;
			if(key.startsWith(extraKeys)){
				if(extraSystemPackages.length()>0){
					extraSystemPackages.append(",");
				}
				extraSystemPackages.append(runtimeContext.getConfiguration().get(key));
			}
		}
	}

	protected void setupFrameworkSpecificConfiguration(){        
    }

    public void startFramework() throws BundleException
    {
        if(logger.isTraceEnabled()) logger.trace( "> startFramework" );
        framework.start();
        waitBundleStartStatus(framework, Bundle.ACTIVE);
        
        BunldeEventListener bunldeEventListener=new BunldeEventListener();
        runtimeContext.setBunldeEventListener( bunldeEventListener );
        if(logger.isTraceEnabled()) logger.trace( "< startFramework" );

    }

	public void startBundles() throws BundleException
    {
        if(logger.isTraceEnabled()) logger.trace( "> startBundles" );
        String bundleGroups=runtimeContext.getConfiguration().retriveXdirOsgiBundlesSystem();
        runBundleGroups( bundleGroups,false);
        if(logger.isTraceEnabled()) logger.trace( "< startBundles" );
    }
    
    public void hotDeployBundles(List<String> bundleFiles)throws BundleException{
        runBundleFiles(bundleFiles , false );
    }


    public void stopFramework() throws BundleException
    {
        stopAndWaitForNoneActive(framework );
    }


    public Framework getFramework()
    {
        return framework;
    }
}
