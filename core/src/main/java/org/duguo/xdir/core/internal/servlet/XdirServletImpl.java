package org.duguo.xdir.core.internal.servlet;


import org.duguo.xdir.core.internal.app.Application;
import org.duguo.xdir.core.internal.cache.CacheService;
import org.duguo.xdir.core.internal.cache.CacheableResponse;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.core.internal.model.PathInfoImpl;
import org.duguo.xdir.http.support.AbstractAliasSupportServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;


public class XdirServletImpl extends AbstractAliasSupportServlet {
    private static final Logger logger = LoggerFactory.getLogger(XdirServletImpl.class);

    protected Application rootApplication;

    protected CacheService cacheService;


    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        long startTimestamp=System.currentTimeMillis();
        if (logger.isTraceEnabled()) logger.trace("> service {}", ((HttpServletRequest) req).getRequestURL());

        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        try {
            handleRequest(request, response);
            if (logger.isTraceEnabled())
                logger.trace("< service successfully [time: {}] {}",System.currentTimeMillis()-startTimestamp, ((HttpServletRequest) req).getRequestURL());
        } catch (Throwable ex) {
            logger.error("Failed to handle request", ex);
            if (logger.isTraceEnabled()) logger.trace("< service with exception {}", ex.getMessage());

            if (ex instanceof ServletException)
                throw (ServletException) ex;
            if (ex instanceof IOException)
                throw (IOException) ex;
        }
    }


    protected void handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String virtualHostKey = buildVirtualHostKey(request);
        Application selectedApp = rootApplication.getChildren().get(virtualHostKey);
        if(selectedApp!=null){
            logger.debug("use virtual host app with key {}", virtualHostKey);
            response = cacheService.handleRequest(request, response);
            if (response == null) {
                return;
            }
        }else{
            selectedApp = rootApplication;
            logger.debug("use default root app");
        }

        try {
            handleRequestWithEmptyCache(request, response,selectedApp);
        } finally {
            if (response instanceof CacheableResponse) {
                CacheableResponse cacheableResponse = (CacheableResponse) response;
                if (!cacheableResponse.isRequestProcessed()) {
                    cacheService.cacheResponse(request, cacheableResponse);
                }
            }
        }
    }


    protected void handleRequestWithEmptyCache(HttpServletRequest request, HttpServletResponse response,Application selectedApp) throws Exception {
        if (logger.isTraceEnabled())
            logger.trace("> handleRequestWithEmptyCache ({} {})", request.getMethod(), request.getRequestURL());
        ModelImpl model = new ModelImpl();

        model.setRequest(request);
        model.setResponse(response);
        setupPathInfo(model);

        int returnStatus = selectedApp.handle(model);
        if (returnStatus < 400) {
            model.getResponse().flushBuffer();
        } else {
            if (!model.getResponse().isCommitted()) {
                model.getResponse().sendError(returnStatus);
            }
        }
        if (logger.isTraceEnabled())
            logger.trace("< handleRequestWithEmptyCache finished with status {}", returnStatus);
    }

    private String buildVirtualHostKey(HttpServletRequest request) {
        String serverName = request.getServerName();
        int port = request.getServerPort();
        StringBuilder virtualHostKey = new StringBuilder(48);
        virtualHostKey.append(serverName);
        if (port != 80) {
            virtualHostKey.append('_');
            virtualHostKey.append(port);
        }
        if (logger.isDebugEnabled())
            logger.debug("built virtual host key {}", virtualHostKey.toString());
        return virtualHostKey.toString();
    }


    protected void setupPathInfo(ModelImpl model) throws Exception {
        String path = URLDecoder.decode(model.getRequest().getRequestURI(), "UTF-8");
        if (path.length() == 1) { // e.g. http://localhost:8080/
            model.setPathInfo(new PathInfoImpl(new String[]{}));
        } else {
            model.setPathInfo(new PathInfoImpl(path.substring(1).split("/")));
        }
        StringBuffer requestURL = model.getRequest().getRequestURL();
        model.getPageContext().append(requestURL.substring(0, requestURL.indexOf("/", 8)));
    }

    /**
     * Can only register as root servlet
     */
    @Override
    public String getAlias() {
        return "/";
    }

    public void setRootApplication(Application rootApplication) {
        this.rootApplication = rootApplication;
    }


    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

}
