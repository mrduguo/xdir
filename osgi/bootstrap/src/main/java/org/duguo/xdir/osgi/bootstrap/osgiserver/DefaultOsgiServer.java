package org.duguo.xdir.osgi.bootstrap.osgiserver;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.duguo.xdir.osgi.bootstrap.spi.Server;
import org.duguo.xdir.osgi.bootstrap.conf.XdirLogConfig;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.launch.FrameworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;


public class DefaultOsgiServer extends AbstractOsgiServer implements Server
{
    private static final Logger logger = LoggerFactory.getLogger(DefaultOsgiServer.class);
	

    public static final String KEY_EXTRA_SYSTEM_PACKAGES="org.osgi.framework.system.packages.extra";

    public void start() throws Exception
    {
        if(logger.isInfoEnabled()) logger.info("Server starting ...");
        try{
            FileUtils.deleteQuietly(new File(System.getProperty("org.osgi.framework.storage")));
            createFramework();
            startFramework();
            startBundles();

            String startupMsg = System.getProperty("xdir.server.started.msg", "Server started");
            logger.info(startupMsg);
            XdirLogConfig.rawStdOut.println(startupMsg);
        }catch (Exception ex){
            logger.error("Server start failed: {}",ex.getMessage(),ex);
            throw ex;
        }
    }


    public void stop() throws Exception
    {
        if(logger.isInfoEnabled()) logger.info("Server stopping ...");
        try{
            framework.stop();
            long stopTimeout=Long.parseLong(System.getProperty("xdir.osgi.stop.timeout","10000"));
            FrameworkEvent result = framework.waitForStop(stopTimeout);
            if(result.getType()==FrameworkEvent.WAIT_TIMEDOUT){
                throw new TimeoutException("Stop OSGi server time out");
            }
            if(logger.isInfoEnabled()) logger.info("Server stopped");
        }catch (Exception ex){
            logger.error("Server stop failed {}",ex.getMessage(),ex);
            throw ex;
        }
    }



    private void createFramework() throws Exception{
        String factoryClassName = retrieveFrameworkFactory();
        if(logger.isDebugEnabled()) logger.debug("framework factory class [{}]",factoryClassName);
        Class factoryClass = FrameworkFactory.class.getClassLoader().loadClass(factoryClassName);
        FrameworkFactory factory = (FrameworkFactory) factoryClass.newInstance();
        Map<String,String> frameworkConfig=new HashMap<String, String>();
        for(Object key:System.getProperties().keySet()){
           String keyString=(String)key;
            if(keyString.startsWith("org.osgi.framework")){
                frameworkConfig.put(keyString,System.getProperty(keyString));
            }
        }
        setupExtraSystemPackages(frameworkConfig);
        setupFrameworkSpecificConfiguration(frameworkConfig);
        
        framework=factory.newFramework(frameworkConfig);
    }

    public void startFramework() throws Exception
    {
        if(logger.isTraceEnabled()) logger.trace( "> startFramework" );
        framework.start();
        waitBundleStartStatus(framework, Bundle.ACTIVE);
        registerBundleListener();

        if(logger.isTraceEnabled()) logger.trace( "< startFramework" );

    }

    private String retrieveFrameworkFactory() throws Exception {
        String factoryConfigurationSource = "/META-INF/services/" + FrameworkFactory.class.getName();
        InputStream inputStream = null;
        try {
            inputStream = FrameworkFactory.class.getResourceAsStream(factoryConfigurationSource);
            return IOUtils.toString(inputStream).trim();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    protected void setupExtraSystemPackages(Map<String, String> frameworkConfig) {
        if(!frameworkConfig.containsKey(KEY_EXTRA_SYSTEM_PACKAGES)){
        	StringBuilder extraSystemPackages=new StringBuilder();
        	
            if("true".equals(System.getProperty("xdir.osgi.export.bootstrap.jars.as.system.packages", "true"))){
        	    setupExtraSystemPackagesScanBootJars(extraSystemPackages);
            }
        	setupExtraSystemPackagesScanExtendedKeys(extraSystemPackages);
        	
            if(logger.isDebugEnabled())
                logger.debug( "Setup  ["+KEY_EXTRA_SYSTEM_PACKAGES+"] with value ["+extraSystemPackages+"]" );
            frameworkConfig.put(KEY_EXTRA_SYSTEM_PACKAGES, extraSystemPackages.toString());
        }else{
        	if(logger.isDebugEnabled())
                logger.debug( "User already provided  ["+KEY_EXTRA_SYSTEM_PACKAGES+"] with value ["+frameworkConfig.get(KEY_EXTRA_SYSTEM_PACKAGES)+"]" );
        }
		
	}

	private void setupExtraSystemPackagesScanBootJars( StringBuilder extraSystemPackages) {
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
	private void setupExtraSystemPackagesScanExtendedKeys( StringBuilder extraSystemPackages) {
		String extraKeys=KEY_EXTRA_SYSTEM_PACKAGES+".";
		for(Object keyObject:System.getProperties().keySet()){
			String key=(String)keyObject;
			if(key.startsWith(extraKeys)){
				if(extraSystemPackages.length()>0){
					extraSystemPackages.append(",");
				}
				extraSystemPackages.append(System.getProperties().get(key));
			}
		}
	}

	protected void setupFrameworkSpecificConfiguration(Map<String, String> frameworkConfig){
        if(logger.isDebugEnabled()){
            logger.debug( "Setup felix configuration felix.log.level=4" );
            frameworkConfig.put("felix.log.level", "4");
        }
    }

    private void registerBundleListener() {
        bunldeEventListener=new BunldeEventListener();
        framework.getBundleContext().addBundleListener(bunldeEventListener);


        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put( "org.eclipse.gemini.blueprint.bean.name", "bootstrapBundleListener" );
        framework.getBundleContext().registerService(
                BundleListener.class.getName(),bunldeEventListener, properties );
        if(logger.isDebugEnabled()) logger.debug( "Published bootstrapBundleListener as service" );
    }

    private void startBundles() throws Exception
    {
        if(logger.isTraceEnabled()) logger.trace("> startBundles");
        runBundleGroups(new File(System.getProperty("xdir.home") + "/bundles"));
        if(logger.isTraceEnabled()) logger.trace( "< startBundles" );
    }

}
