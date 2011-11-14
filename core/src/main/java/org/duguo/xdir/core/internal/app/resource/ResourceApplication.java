package org.duguo.xdir.core.internal.app.resource;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.core.internal.app.JcrTemplateAwareApplication;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.core.internal.resource.Resource;
import org.duguo.xdir.core.internal.resource.ResourceLoader;
import org.duguo.xdir.util.io.FileUtil;


public class ResourceApplication extends JcrTemplateAwareApplication implements ResourceService
{
    private static final Logger logger = LoggerFactory.getLogger( ResourceApplication.class );

    private static final String CSS_KEY_PREFIX="xdir.css.";
    private List<String> allResources;
    private ResourceLoader resourceLoader;
    private List<String> templateFormats;
    
    @Override
    public ResourceService getResource()
    {
        return this;
    }
    

    protected int handleInSession( ModelImpl model, int handleStatus ) throws Exception
    {
        String resourcePath = model.getPathInfo().getRemainPath().substring( 1 );
        String format = null;
        int dotPosition = resourcePath.lastIndexOf( '.' );
        if(dotPosition>0){
            format = resourcePath.substring( dotPosition );
            if(format.indexOf( "/" )>0){
                format=null;
            }
        }
        if(format!=null){
            if ( templateFormats.contains( format ) || model.getApp().getFormats().containsKey( format ) )
            {
                resourcePath = resourcePath.substring( 0, dotPosition );
                model.setFormat( format );

                if ( logger.isDebugEnabled() )
                    logger.debug( "process resource template [{}]", resourcePath );
                handleStatus = getTemplate().process( model, resourcePath );
            }
            else
            {
                handleStatus = handleRawResource( model, handleStatus, resourcePath );
            }
        }

        return handleStatus;
    }


    protected int handleRawResource( ModelImpl model, int handleStatus, String resourcePath ) throws Exception
    {
        Resource resource = resourceLoader.loadResource( model, resourcePath );
        if ( resource != null )
        {
            if ( logger.isDebugEnabled() )
                logger.debug( "write raw resource [{}]", resourcePath );
            
            InputStream inputStream = resource.getAsInputStream();
            try
            {
                FileUtil.writeStream( inputStream, model.getResponse().getOutputStream() );
                handleStatus = STATUS_SUCCESS;
            }
            finally
            {
                inputStream.close();
            }
        }
        return handleStatus;
    }


    public void loadCssConf( ModelImpl model) throws Exception
    {
        loadCssConfFromProperties( model, getProps().getProperties() );
        loadCssConfFromProperties( model, System.getProperties());
    }


    protected void loadCssConfFromProperties( ModelImpl model,Map<Object, Object> properties) throws Exception
    {
        for(Entry<Object, Object> currentEntry: properties.entrySet()){
            String key=(String)currentEntry.getKey();
            if(key.startsWith( CSS_KEY_PREFIX)){
                key=key.substring( CSS_KEY_PREFIX.length());
                key=key.replaceAll( "\\.", "_");

                String value=(String)currentEntry.getValue();
                value=getProps().resolveStringValue( value );
                try{
                    int intValue=Integer.parseInt( value );
                    model.put( key, intValue );
                }catch(NumberFormatException ex){
                    model.put( key, value );                    
                }
                if(logger.isDebugEnabled())
                    logger.debug("loaded css conf [{}={}]",key,value);
            }
        }
    }


    public String getResourceUrl( ModelImpl model,String resourceName )
    {
        return getSite().getBaseUrl()+resourceName;
    }


    public List<String> getAllResources()
    {
        return allResources;
    }


    public void setAllResources( List<String> allResources )
    {
        this.allResources = allResources;
    }


    public ResourceLoader getResourceLoader()
    {
        return resourceLoader;
    }


    public void setResourceLoader( ResourceLoader resourceLoader )
    {
        this.resourceLoader = resourceLoader;
    }


    public void setTemplateFormats( List<String> templateFormats )
    {
        this.templateFormats = templateFormats;
    }
}
