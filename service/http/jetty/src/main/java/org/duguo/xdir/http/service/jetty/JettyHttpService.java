package org.duguo.xdir.http.service.jetty;


import org.duguo.xdir.http.service.impl.HttpContextAwareResourcesServlet;
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
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.util.*;


public class JettyHttpService implements HttpService, ApplicationContextAware
{

    private static final Logger logger = LoggerFactory.getLogger( JettyHttpService.class );
    private final Map<String, ServletHolder> registedServlets = new Hashtable<String, ServletHolder>();
    
    private ServletContextHandler servletContextHandler;
    private HttpContext defaultHttpContext;
    private ApplicationContext applicationContext;
    private int initOrder=1;
    private boolean startWithUnavailable=false;

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

        ServletHolder servletHolder =applicationContext.getBean("servletHolderPrototype",ServletHolder.class);
        servletHolder.setServlet(servlet);
        servletHolder.setInitOrder(initOrder);
        registedServlets.put( alias, servletHolder );


        servletContextHandler.addServlet(servletHolder, alias);
        if(servletHolder.getUnavailableException()!=null && !startWithUnavailable){
            logger.error("Failed to register servlet, will force exit",servletHolder.getUnavailableException());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // it will trigger shutdown hook
                    // @link CustomListenerAdapterUtils doesn't propagate the exception to the host bundle
                    System.exit(-1);
                }
            }).start();
        }else{
            if(logger.isDebugEnabled()) logger.debug( "registered servlet alias [" + alias + "]" );
        }
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    public void setInitOrder(int initOrder) {
        this.initOrder = initOrder;
    }

    public void setStartWithUnavailable(boolean startWithUnavailable) {
        this.startWithUnavailable = startWithUnavailable;
    }
}
