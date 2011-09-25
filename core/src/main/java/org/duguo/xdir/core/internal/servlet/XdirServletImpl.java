package org.duguo.xdir.core.internal.servlet;


import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.core.internal.app.Application;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.core.internal.model.PathInfoImpl;
import org.duguo.xdir.http.support.AbstractAliasSupportServlet;


public class XdirServletImpl  extends AbstractAliasSupportServlet implements XdirServlet
{
    private static final Logger logger = LoggerFactory.getLogger( XdirServletImpl.class );
    
    private Application application;
    
    public void service( ServletRequest req, ServletResponse res ) throws ServletException, IOException
    {
        HttpServletRequest request = ( HttpServletRequest ) req;
        HttpServletResponse response = ( HttpServletResponse ) res;
        try{
            ModelImpl model=buildModel( request, response );
            if(model!=null){
                handleRequest(model);
            }         
        }catch(Exception ex){
            logger.error( "Failed to handle request", ex);
        }
    }
    
    protected  void handleRequest( ModelImpl model ) throws Exception{
        if(logger.isDebugEnabled())
            logger.debug("servlet handleRequest with full path:"+model.getPathInfo().getFullPath());
        int returnStatus=application.handle( model );
        if(returnStatus<400){
            model.getResponse().flushBuffer();
        }else{
            if(!model.getResponse().isCommitted()){
                model.getResponse().sendError( returnStatus );
            }            
        }
        if(logger.isDebugEnabled())
            logger.debug("servlet handleRequest finished with status:"+returnStatus);
    }

    protected ModelImpl buildModel( HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        ModelImpl model=new ModelImpl();
        String path = buildPath( request,model );
        if(path==null){
            response.sendError( 404 );
            return null;
        }else if(path.length()==0){
            model.setPathInfo( new PathInfoImpl(new String[]{}));            
        }else{
            model.setPathInfo( new PathInfoImpl( path.split( "/" ) ));
        }
        if(logger.isDebugEnabled())
            logger.debug( "buildModel with path ["+path+"]");
        
        model.setRequest( request );
        model.setResponse( response );
        return model;
    }
    
    protected String buildPath( HttpServletRequest request,ModelImpl model ) throws Exception
    {
        String path=URLDecoder.decode(request.getRequestURI(),"UTF-8");
        path=path.substring(getBasePathLength());
        return path;
    }

    public Application getApplication()
    {
        return application;
    }


    public void setApplication( Application application )
    {
        this.application = application;
    }

}
