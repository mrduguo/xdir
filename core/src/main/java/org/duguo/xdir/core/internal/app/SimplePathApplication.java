package org.duguo.xdir.core.internal.app;


import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.spi.model.support.AbstractGetAndPut;
import org.duguo.xdir.core.internal.model.ModelImpl;


public class SimplePathApplication extends AbstractGetAndPut implements Application
{
    private static final Logger logger = LoggerFactory.getLogger( SimplePathApplication.class );

    private Map<String, Application> children=new HashMap<String, Application>();
    private Application parent;

    public int handle( ModelImpl model ) throws Exception
    {
        Application application=children.get( model.getPathInfo().getCurrentPath() );
        if(application!=null){
            model.getPageContext().append("/");
            model.getPageContext().append(model.getPathInfo().getCurrentPath());
            model.getPathInfo().moveToNextPath();
            return application.handle( model );
        }else{
            return execute( model );
        }
    }

    public int execute( ModelImpl model ) throws Exception
    {
        model.getPathInfo().moveToPreviousPath();
        model.getPageContext().delete(model.getPageContext().lastIndexOf("/"),model.getPageContext().length());
        return getParent().execute(model);
    }

    protected int handleInternalException( ModelImpl model, int handleStatus,Throwable rawException )
    {
        logger.error( "failed to handle application request",rawException );
        if(model.getResponse().isCommitted()){
            logger.info( "response already committed, ignore error handling");
            handleStatus=STATUS_SUCCESS;
        }else{
            model.put( "exception", rawException );
            handleStatus = handleErrorCode( model, handleStatus );
        }
        return handleStatus;
    }

    protected int handlePageNotFound( ModelImpl model )
    {
        int handleStatus = handleErrorCode( model, STATUS_PAGE_NOT_FOUND );
        return handleStatus;
    }

    protected int handleErrorCode( ModelImpl model, int handleStatus )
    {
        try
        {
            model.getResponse().sendError( handleStatus );
            return STATUS_SUCCESS;
        }
        catch ( Exception ex )
        {
            logger.error( "failed to handle status code ["+handleStatus+"]",ex );
        }
        return handleStatus;
    }
    

    public Map<String, Application> getChildren()
    {
        return children;
    }

    public void setChildren( Map<String, Application> children )
    {
        this.children = children;
    }

    public Application getParent()
    {
        return parent;
    }

    public void setParent( Application parent )
    {
        this.parent = parent;
    }

}
