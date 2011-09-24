package org.duguo.xdir.http.service.impl;


import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.resource.Resource;
import org.osgi.service.http.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.http.support.AbstractAliasSupportServlet;


public class HttpContextAwareResourcesServlet extends AbstractAliasSupportServlet
{
    private static final Logger logger = LoggerFactory.getLogger( HttpContextAwareResourcesServlet.class );
    private HttpContext httpContext;

    public HttpContextAwareResourcesServlet()
    {        
    }
    public HttpContextAwareResourcesServlet( String alias, String servletInfo, HttpContext httpContext)
    {
        setAlias( alias );
        setServletInfo( servletInfo );
        setHttpContext( httpContext );
    }


    public void service( ServletRequest req, ServletResponse res ) throws ServletException, IOException
    {
        if(logger.isDebugEnabled())
            logger.debug( "servlet ["+getServletInfo()+"] called on service()");
        
        HttpServletRequest httpServletRequest = ( HttpServletRequest ) req;
        HttpServletResponse httpServletResponse = ( HttpServletResponse ) res;
        
        String resourceName = httpServletRequest.getRequestURI().substring(getBasePathLength());
        URL resourceUrl = httpContext.getResource( resourceName );
        if ( resourceUrl != null )
        {
            Resource outputResource = Resource.newResource( resourceUrl );
            if ( outputResource.exists())
            {
                if(outputResource.isDirectory()){
                    handleDirectory(httpServletRequest,httpServletResponse,resourceName,outputResource);
                }else{
                    handleFile(httpServletRequest,httpServletResponse,resourceName,outputResource);
                }
                return;
            }
        }
        handleResourceotFound( httpServletRequest,httpServletResponse );
    }


    protected void handleDirectory( HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
        String resourceName,Resource outputResource )throws IOException
    {
        if(logger.isDebugEnabled())
            logger.debug( "handleDirectory ["+resourceName+"]");
        handleResourceotFound( httpServletRequest,httpServletResponse );
    }


    protected void handleFile( HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
        String resourceName,Resource outputResource )throws IOException
    {
        setupMimeType( httpServletResponse, resourceName );
        writeResource( httpServletResponse, outputResource );
    }


    protected void handleResourceotFound(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse ) throws IOException
    {
        httpServletResponse.sendError( 404 );
        if(logger.isDebugEnabled())
            logger.debug( "handleResourceotFound and returned 404 to client");
    }


    protected void writeResource( HttpServletResponse httpServletResponse, Resource outputResource ) throws IOException
    {
        outputResource.writeTo( httpServletResponse.getOutputStream(), 0, outputResource.length() );
        if(logger.isDebugEnabled())
            logger.debug( "writeResource finished");
    }


    protected void setupMimeType( HttpServletResponse httpServletResponse, String resourceName )
    {
        String mimeType = httpContext.getMimeType( resourceName );
        if ( mimeType != null )
        {
            httpServletResponse.setContentType( mimeType );
            if(logger.isDebugEnabled())
                logger.debug( "set reponse mimetype ["+mimeType+"] for ["+resourceName+"]");
        }
    }


    public HttpContext getHttpContext()
    {
        return httpContext;
    }


    public void setHttpContext( HttpContext httpContext )
    {
        this.httpContext = httpContext;
    }


}
