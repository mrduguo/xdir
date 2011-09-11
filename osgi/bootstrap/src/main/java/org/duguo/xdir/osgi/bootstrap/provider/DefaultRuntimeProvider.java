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
import org.duguo.xdir.osgi.spi.conditional.ConditionalService;
import org.duguo.xdir.osgi.spi.util.ClassUtil;
import org.duguo.xdir.osgi.bootstrap.conditional.ConditionalServiceImpl;
import org.duguo.xdir.osgi.bootstrap.conditional.term.BundleTerm;
import org.duguo.xdir.osgi.bootstrap.conditional.term.ServiceTerm;
import org.duguo.xdir.osgi.bootstrap.event.BunldeEventListener;
import org.duguo.xdir.osgi.bootstrap.launcher.RuntimeContext;
import org.duguo.xdir.osgi.bootstrap.log.Logger;


public class DefaultRuntimeProvider extends AbstractRuntimeProvider
{
	

    public static final String KEY_EXTRA_SYSTEM_PACKAGES="org.osgi.framework.system.packages.extra";

    public void createFramework(RuntimeContext runtimeContext, FrameworkFactory factory )
    {
        this.runtimeContext=runtimeContext;
        
        setupExtraSystemPackages();
        
        setupFrameworkSpecificConfiguration();

        Logger.debug( "Creating OSGi framework" );
        framework = factory.newFramework( runtimeContext.getConfiguration());
        runtimeContext.setFramework( framework );
        initBundlesCache();
        Logger.debug( "Created OSGi framework" );        
    }

    protected void setupExtraSystemPackages() {

        if(!runtimeContext.getConfiguration().containsKey(KEY_EXTRA_SYSTEM_PACKAGES)){
        	StringBuilder extraSystemPackages=new StringBuilder();
        	
        	setupExtraSystemPackagesScanBootJars(extraSystemPackages);
        	
        	setupExtraSystemPackagesScanExtendedKeys(extraSystemPackages);
        	
            if(Logger.isDebugEnabled())
                Logger.debug( "Setup  ["+KEY_EXTRA_SYSTEM_PACKAGES+"] with value ["+extraSystemPackages+"]" );
            runtimeContext.getConfiguration().put( KEY_EXTRA_SYSTEM_PACKAGES, extraSystemPackages.toString() );
        }else{
        	if(Logger.isDebugEnabled())
                Logger.debug( "User already provided  ["+KEY_EXTRA_SYSTEM_PACKAGES+"] with value ["+runtimeContext.getConfiguration().get(KEY_EXTRA_SYSTEM_PACKAGES)+"]" );
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
        Logger.debug( "Starting OSGi framework" );
        framework.start();
        waitBundleStartStatus( framework, Bundle.ACTIVE );
        Logger.debug( "Started OSGi framework" );
        
        BunldeEventListener bunldeEventListener=new BunldeEventListener();
        runtimeContext.setBunldeEventListener( bunldeEventListener );
      
        createConditionalService();
    }


    protected void createConditionalService() {
    	ConditionalService conditionalService=ClassUtil.loadRequiredInstanceFromSystemProperty(ConditionalService.class, ConditionalServiceImpl.class, ConditionalServiceImpl.KEY_XDIR_OSGI_CONDITIONAL_IMPL);
    	runtimeContext.conditionalService=conditionalService;
    	conditionalService.registTerm("service", new ServiceTerm(framework));
    	conditionalService.registTerm("bundle", new BundleTerm(framework));
	}

	public void startSystemBundles() throws BundleException
    {
        Logger.debug( "Starting system bundles" );
        String bundleGroups=runtimeContext.getConfiguration().retriveXdirOsgiBundlesSystem();
        runBundleGroups( bundleGroups,false);
        Logger.debug( "Started system bundles" );
    }


    public boolean startUserBundles() throws BundleException
    {
        Logger.debug( "Starting user bundles" );
        String bundleGroups=runtimeContext.getConfiguration().retriveXdirOsgiBundlesUser();
        boolean success=runBundleGroups( bundleGroups,true);
        Logger.debug( "Started user bundles" );
        return success;
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
