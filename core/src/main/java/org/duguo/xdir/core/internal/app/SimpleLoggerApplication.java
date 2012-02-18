package org.duguo.xdir.core.internal.app;


import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.spi.model.support.AbstractGetAndPut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


/**
 * To log the request details to default log at info level
 */
public class SimpleLoggerApplication extends AbstractGetAndPut implements Application
{
    private Logger logger = LoggerFactory.getLogger( SimpleLoggerApplication.class );

    private Map<String, Application> children=new HashMap<String, Application>();
    private Application parent;

    public int handle( ModelImpl model ) throws Exception
    {
        if(logger.isInfoEnabled()){
            HttpServletRequest request = model.getRequest();
            StringBuilder logLine=new StringBuilder(request.getRequestURL());
            logLine.append("?");
            logLine.append(request.getQueryString());
            logLine.append(" - ");
            Enumeration headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()){
                String headerName=(String)headerNames.nextElement();
                logLine.append(headerName);
                logLine.append("=");

                logLine.append(request.getHeader(headerName));
                logLine.append(",");
            }
            logLine.delete(logLine.length()-1,logLine.length());
            logger.info(logLine.toString());
        }
        return STATUS_SUCCESS;
    }

    public int execute( ModelImpl model ) throws Exception
    {
        // never happens as already handled
        return STATUS_PAGE_NOT_FOUND;
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

    public void setLoggerName( String loggerName )
    {
        logger=LoggerFactory.getLogger(loggerName);
    }

}
