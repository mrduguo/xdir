package org.duguo.xdir.http.service.impl;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.eclipse.jetty.http.MimeTypes;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundleAwareHttpContext implements HttpContext,BundleContextAware
{
    private static final Logger logger = LoggerFactory.getLogger( BundleAwareHttpContext.class );

    private BundleContext bundleContext;
    private String resourceBase;
    private MimeTypes mimeTypes = new MimeTypes();

    public String getMimeType( String name )
    {
        if(logger.isDebugEnabled())
            logger.debug( "getMimeType ["+name+"]" );
        return mimeTypes.getMimeByExtension( name ).toString();
    }

    public URL getResource( String name )
    {
        URL resourceUrl=null;
        if(bundleContext!=null){
            if(resourceBase!=null){
                resourceUrl=bundleContext.getBundle().getResource( resourceBase+name );
            }else{
                resourceUrl=bundleContext.getBundle().getResource( name );
            }
        }
        if(logger.isDebugEnabled())
            logger.debug( "geResource ["+name+"] and return ["+resourceUrl+"]" );
        return resourceUrl;
    }


    public boolean handleSecurity( HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        throw new UnsupportedOperationException("handleSecurity is unsupported for "+BundleAwareHttpContext.class.getName());
    }    
    
    public void setMimeTypes( MimeTypes mimeTypes )
    {
        this.mimeTypes = mimeTypes;
    }


    public void setBundleContext( BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

    public void setResourceBase( String resourceBase )
    {
        this.resourceBase = resourceBase;
    }

}
