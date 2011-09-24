package org.duguo.xdir.http.service.jetty;


import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletMapping;
import org.eclipse.jetty.util.LazyList;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.http.service.impl.HttpContextAwareResourcesServlet;


public class JettyHttpService implements HttpService
{

    private static final Logger logger = LoggerFactory.getLogger( JettyHttpService.class );
    private final Map<String, ServletHolder> registedServlets = new Hashtable<String, ServletHolder>();
    
    private ServletContextHandler servletContextHandler;
    private HttpContext defaultHttpContext;

    public HttpContext createDefaultHttpContext()
    {
        if(logger.isDebugEnabled())
            logger.debug( "createDefaultHttpContext invoked return ["+defaultHttpContext+"]" );
        return defaultHttpContext;
    }


    public void registerResources( String alias, String name, HttpContext context ) throws NamespaceException
    {
        if(logger.isDebugEnabled())
            logger.debug( "registering resources alias [" + alias + "] name ["+name+"]" );
        try{
            registerServlet( alias, new HttpContextAwareResourcesServlet( alias,name,context), null, context );
        }catch(ServletException ex){
            throw new RuntimeException("Failed to register resources ["+alias+"] as servlet",ex);
        }
        if(logger.isDebugEnabled())
            logger.debug( "registered resources alias [" + alias + "] name ["+name+"]" );
    }


    @SuppressWarnings("unchecked")
    public void registerServlet( String alias, Servlet servlet, Dictionary initparams, HttpContext context )
        throws ServletException, NamespaceException
    {
        if(logger.isDebugEnabled())
            logger.debug( "registering servlet alias [" + alias + "]" );
        if ( registedServlets.containsKey( alias ) )
        {
            throw new RuntimeException( "Servlet alias [" + alias + "] already registed and cannot register again" );
        }

        ServletHolder servletHolder = new ServletHolder( servlet );
        registedServlets.put( alias, servletHolder );


        servletContextHandler.addServlet( servletHolder, alias );
        if(logger.isDebugEnabled())
            logger.debug( "registered servlet alias [" + alias + "]" );
    }

    public void unregister( String alias )
    {
        if(logger.isDebugEnabled())
            logger.debug( "unregistering alias [" + alias + "]" );
        if ( registedServlets.containsKey( alias ) )
        {
            removeServletFromServletHandler( alias );
        }
        else
        {
            logger.warn( "Servlet alias [" + alias + "] not registed and cannot unregister" );
        }
    }

    private void removeServletFromServletHandler( String alias )
    {
        ServletHandler servletHandler=servletContextHandler.getServletHandler();
        ServletHolder servletHolderToUnregister = registedServlets.get( alias );
        ServletMapping servletMappingToUnregister = findServletMapping( alias, servletHandler );

        ServletMapping[] remainMappings = (ServletMapping[])LazyList.removeFromArray( servletHandler.getServletMappings(), servletMappingToUnregister);
        servletHandler.setServletMappings( remainMappings );
        
        ServletHolder[] remainServlets = (ServletHolder[])LazyList.removeFromArray( servletHandler.getServlets(), servletHolderToUnregister);
        
        servletHandler.setServlets( remainServlets );
        
        registedServlets.remove( alias );
        if(logger.isDebugEnabled())
            logger.debug( "unregistered alias [" + alias + "]" );
    }


    private ServletMapping findServletMapping( String alias, ServletHandler servletHandler )
    {
        for (ServletMapping servletMapping:servletHandler.getServletMappings())
        {
            if ( alias.equals(servletMapping.getPathSpecs()[0]) )
            {
                return servletMapping;
            }
        }
        return null;
    }


    public void setServletContextHandler( ServletContextHandler servletContextHandler )
    {
        this.servletContextHandler = servletContextHandler;
    }


    public void setDefaultHttpContext( HttpContext defaultHttpContext )
    {
        this.defaultHttpContext = defaultHttpContext;
    }

}
