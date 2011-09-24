package org.duguo.xdir.http.service.impl;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;

import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.http.service.ServletService;

public class ServletServiceImpl implements ServletService
{
    private static final Logger logger = LoggerFactory.getLogger( ServletServiceImpl.class );
    
    @SuppressWarnings("unchecked")
    private Dictionary initparams;
    private HttpContext context;
    private Map<String, Servlet> servlets=new HashMap<String, Servlet>();

    private HttpService httpService;
    
    
    public void onBind(HttpService httpService) throws Exception{
        if(logger.isDebugEnabled())
            logger.debug( "onBind called" );
        this.httpService=httpService;
        for(Map.Entry<String, Servlet> entry:servlets.entrySet()){
            if(logger.isDebugEnabled())
                logger.debug( "onBind registerServlet ["+entry.getKey()+"]" );
            httpService.registerServlet( entry.getKey(), entry.getValue(), initparams, context );
        }
    }
    
    public void onUnbind(HttpService httpService) throws Exception{
        if(logger.isDebugEnabled())
            logger.debug( "onUnbind called" );
        for(Map.Entry<String, Servlet> entry:servlets.entrySet()){
            if(logger.isDebugEnabled())
                logger.debug( "onUnbind unregister ["+entry.getKey()+"]" );
            httpService.unregister( entry.getKey());
        }
        this.httpService=null;
    }
    
    public void register(String alias, Servlet servlet) throws Exception{
        if(httpService!=null){
            if(servlets.containsKey( alias )){
                unregister(alias);
            }
            httpService.registerServlet( alias, servlet, initparams, context );
            if(logger.isDebugEnabled())
                logger.debug( "registered servlet [{}] to httpservice" , alias);
        }
        servlets.put( alias, servlet );
        if(logger.isDebugEnabled())
            logger.debug( "registered servlet [{}]" , alias);
    }
    
    public void unregister(String alias) throws Exception{
        if(servlets.containsKey( alias )){
            if(httpService!=null){
                httpService.unregister( alias);
                if(logger.isDebugEnabled())
                    logger.debug( "unregistered servlet [{}] from httpservice" , alias);
            }
            servlets.remove( alias );
            if(logger.isDebugEnabled())
                logger.debug( "unregistered servlet [{}]" , alias);
        }else{
            logger.warn( "unregister none exist servlet [{}]" , alias);            
        }
    }

    @SuppressWarnings("unchecked")
    public void setInitparams( Dictionary initparams )
    {
        this.initparams = initparams;
    }

    public void setContext( HttpContext context )
    {
        this.context = context;
    }

    public void setServlets( Map<String, Servlet> servlets )
    {
        this.servlets = servlets;
    }

}
