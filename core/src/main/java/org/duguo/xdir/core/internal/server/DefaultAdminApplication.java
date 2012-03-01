package org.duguo.xdir.core.internal.server;


import java.io.File;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.osgi.framework.Constants;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.duguo.xdir.core.internal.app.BestPathMatchApplication;
import org.duguo.xdir.spi.util.collection.MapUtil;
import org.duguo.xdir.spi.util.io.FileUtil;


public class DefaultAdminApplication extends BestPathMatchApplication implements BundleContextAware
{
    private static final Logger logger = LoggerFactory.getLogger( DefaultAdminApplication.class );

    private BundleContext bundleContext;

    public String readResourceAsString(String resourceLocation) throws Exception{
        InputStream resourceStream=readResourceAsStream(resourceLocation);
        if(resourceStream!=null){
            String resourceString=FileUtil.readStreamAsString( resourceStream );
            if(logger.isDebugEnabled())
                logger.debug("resource read from [{}]",resourceLocation);
            return resourceString;            
        }else{
            return "";
        }
    }
    
    public InputStream readResourceAsStream(String resourceLocation) throws Exception{
        Resource resource=loadResource( resourceLocation);
        if(resource!=null){
            InputStream resourceStream=resource.getInputStream();
            if(logger.isDebugEnabled())
                logger.debug("input stream read from [{}]",resourceLocation);
            return resourceStream;
        }else{
            return null;
        }
    }
    
    public Resource loadResource(String resourceLocation) throws Exception{
        DefaultResourceLoader resourceLoader=new DefaultResourceLoader();
        Resource resource=resourceLoader.getResource( getPropertiesService().resolveStringValue(resourceLocation) );
        if(resource.exists()){
            if(logger.isDebugEnabled())
                logger.debug("resource [{}] found",resourceLocation);
            return resource;
        }else{
            if(logger.isDebugEnabled())
                logger.debug("resource [{}] not found",resourceLocation);
            return null;
        }
    }
    
    public void saveResource(String filePath,String textContent) throws Exception{
        File realFile=new File(getPropertiesService().resolveStringValue(filePath));
        String contentWithoutLr=textContent.replaceAll( "\r", "" );
        FileUtils.writeStringToFile( realFile, contentWithoutLr );
        if(logger.isDebugEnabled())
            logger.debug("resource saved at [{}]:\n{}",filePath,contentWithoutLr);
    }
    
    public Bundle retriveBundle(String bundleInfo) throws Exception{
        Bundle bundle = null;
        try
        {
            long bundleId=Long.parseLong( bundleInfo );
            bundle=retriveBundleById( bundleId );
        }
        catch ( NumberFormatException ex )
        {
            bundle = retriveBundleByName(  bundleInfo);
        }
        if(logger.isDebugEnabled()){
            if(bundle!=null){
                logger.debug("retrived bundle [{}]",bundleInfo);
            }else{
                logger.debug("not found bundle [{}]",bundleInfo);                  
            }
        }
        return bundle;
    }
    
    public Bundle retriveBundleById(long bundleId) throws Exception{
        Bundle bundle = bundleContext.getBundle( bundleId );
        if(logger.isDebugEnabled()){
            if(bundle!=null){
                logger.debug("retrived bundle by id [{}]",bundleId);               
            }else{
                logger.debug("not found bundle by id [{}]",bundleId);                  
            }
        }
        return bundle;
    }

    public Bundle retriveBundleByName(String bundleInfo)
    {
        Bundle bundle = null;
        for ( Bundle currentBundle : bundleContext.getBundles() )
        {
            String bundleKey = currentBundle.getSymbolicName() + "-" + currentBundle.getVersion();
            if ( bundleKey.equals( bundleInfo ) )
            {
                bundle=currentBundle;
                break;
            }else if(bundleKey.startsWith( bundleInfo )){
                if(bundle!=null){
                    throw new RuntimeException( "multiple version found for bundle [" + bundleInfo
                        + "], you may append version e.g. org.foo.bar-1.8.8 to avoid the error" );                    
                }
                bundle=currentBundle;
            }
        }
        if(logger.isDebugEnabled()){
            if(bundle!=null){
                logger.debug("retrived bundle by name [{}]",bundleInfo);               
            }else{
                logger.debug("not found bundle by name [{}]",bundleInfo);                  
            }
        }
        return bundle;
    }

    public Bundle[] listBundles() throws Exception
    {
        Bundle[] bundles = bundleContext.getBundles();
        if ( logger.isDebugEnabled() )
            logger.debug( "list bundles [{}]", bundles.length );
        return bundleContext.getBundles();
    }

    public ServiceReference retriveServiceReference( String filter ) throws Exception
    {
        ServiceReference[] serviceReferences = bundleContext.getServiceReferences( null, filter);
        if ( logger.isDebugEnabled() )
            logger.debug( "retrived service reference for filter [{}]", filter );
        return serviceReferences[0];
    }

    @SuppressWarnings("unchecked")
    public Map listServiceReferences() throws Exception
    {
        Map<Object,ServiceReference> serviceReferences = new Hashtable<Object,ServiceReference>();
        for(Bundle bundle:bundleContext.getBundles()){
            ServiceReference[] references=bundle.getRegisteredServices();
            if(references!=null && references.length>0){
                for ( ServiceReference serviceReference : references)
                {
                    serviceReferences.put(serviceReference.getProperty( Constants.SERVICE_ID ), serviceReference );
                }
            }
        }
        if ( logger.isDebugEnabled() )
            logger.debug( "list service references [{}]", serviceReferences.size() );
        return MapUtil.sort( serviceReferences );
    }

    public ServiceReference retriveSpringServiceReference( String filter ) throws Exception
    {   
        //(&(Bundle-SymbolicName=xdir.core)(Bundle-Version=1.1.1))
        ServiceReference[] serviceReferences = bundleContext.getServiceReferences( BeanFactory.class.getName(), filter);
        if ( logger.isDebugEnabled() )
            logger.debug( "retrived spring service reference for filter [{}]", filter );
        return serviceReferences[0];
    }

    @SuppressWarnings("unchecked")
    public Map listSpringServiceReferences() throws Exception
    {
        Map<Object,ServiceReference> serviceReferences = new Hashtable<Object,ServiceReference>();
        for(ServiceReference serviceReference:bundleContext.getAllServiceReferences(  BeanFactory.class.getName(),null )){
            serviceReferences.put(serviceReference.getProperty( Constants.SERVICE_ID ), serviceReference );
        }
        if ( logger.isDebugEnabled() )
            logger.debug( "list spring service references [{}]", serviceReferences.size() );
        return MapUtil.sort( serviceReferences );
    }


    public void setBundleContext( BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }


    public BundleContext getBundleContext()
    {
        return bundleContext;
    }
}
