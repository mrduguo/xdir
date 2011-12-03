package org.duguo.xdir.core.internal.servlet;


import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duguo.xdir.util.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.core.internal.app.Application;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.core.internal.model.PathInfoImpl;
import org.duguo.xdir.http.support.AbstractAliasSupportServlet;


public class XdirServletImpl  extends AbstractAliasSupportServlet
{
    private static final Logger logger = LoggerFactory.getLogger( XdirServletImpl.class );
    
    protected Application rootApplication;


    public void service( ServletRequest req, ServletResponse res ) throws ServletException, IOException
    {
        if(logger.isTraceEnabled())
            logger.trace("> service");

        HttpServletResponse response = ( HttpServletResponse ) res;
        HttpServletRequest request = ( HttpServletRequest ) req;
        try{
            handleRequest( request, response );
            if(logger.isTraceEnabled())
                logger.trace("< service successfully");
        }catch(Throwable ex){
            logger.error( "Failed to handle request", ex);
            if(logger.isTraceEnabled())
                logger.trace("< service with exception {}",ex.getMessage());

            if(ex instanceof ServletException)
                throw (ServletException)ex;
            if(ex instanceof IOException)
                throw (IOException)ex;
        }
    }



    protected int handleWithVirtualHostAware(ModelImpl model) throws Exception {
        String scheme = model.getRequest().getScheme();
        String serverName = model.getRequest().getServerName();
        int port = resolveNoneStandardPort(model.getRequest(), scheme);

        StringBuilder hostUrl = buildHostUrl(scheme, serverName, port);
        String virtualHostKey = buildVirtualHostKey(scheme, serverName, port);

        model.setPageContext(hostUrl);
        setupPathInfo( model);

        Application selectApp=rootApplication.getChildren().get(virtualHostKey);
        if (selectApp==null) {
           selectApp=rootApplication;
        }
        return selectApp.handle( model );
    }

    private int resolveNoneStandardPort(HttpServletRequest request, String scheme) {
        int port = request.getServerPort();
        if((scheme.equalsIgnoreCase(HttpUtil.HTTP) && port == 80) || (scheme.equalsIgnoreCase(HttpUtil.HTTPS) && port == 443)){
             port=0;
        }
        return port;
    }

    private StringBuilder buildHostUrl(String scheme, String serverName, int port) {
        StringBuilder hostUrl = new StringBuilder(48);
        hostUrl.append(scheme);
        hostUrl.append("://");
        hostUrl.append(serverName);
        if (port > 0) {
            hostUrl.append(':');
            hostUrl.append(port);
        }
        return hostUrl;
    }

    private String buildVirtualHostKey(String scheme, String serverName, int port) {
        StringBuilder virtualHostKey = new StringBuilder(48);
        virtualHostKey.append(scheme);
        virtualHostKey.append("_");
        virtualHostKey.append(serverName);
        if (port > 0) {
            virtualHostKey.append('_');
            virtualHostKey.append(port);
        }
        if(logger.isDebugEnabled())
            logger.debug("built virtual host key {}",virtualHostKey.toString());
        return virtualHostKey.toString();
    }


    protected  void handleRequest(HttpServletRequest request, HttpServletResponse response ) throws Exception{
        if(logger.isTraceEnabled())
            logger.trace("> handleRequest ({} {})",request.getMethod(),request.getRequestURL());


        ModelImpl model=new ModelImpl();

        model.setRequest( request );
        model.setResponse( response );

        int returnStatus=handleWithVirtualHostAware( model );

        if(returnStatus<400){
            model.getResponse().flushBuffer();
        }else{
            if(!model.getResponse().isCommitted()){
                model.getResponse().sendError( returnStatus );
            }
        }

        if(logger.isTraceEnabled())
            logger.trace("< handleRequest finished with status {}",returnStatus);
    }

    
    protected void setupPathInfo(ModelImpl model ) throws Exception
    {
        String path= URLDecoder.decode(model.getRequest().getRequestURI(),"UTF-8");
        if(path.length()==1){ // e.g. http://localhost:8080/
            model.setPathInfo( new PathInfoImpl(new String[]{}));
        }else{
            model.setPathInfo( new PathInfoImpl( path.substring(1).split( "/" ) ));
        }
    }

    /**
     * Can only register as root servlet
     */
    @Override
    public String getAlias() {
        return "/";
    }

    public void setRootApplication( Application rootApplication )
    {
        this.rootApplication = rootApplication;
    }

}
