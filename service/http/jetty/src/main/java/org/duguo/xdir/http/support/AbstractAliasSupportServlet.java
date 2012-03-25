package org.duguo.xdir.http.support;


import org.duguo.xdir.spi.util.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;


public abstract class AbstractAliasSupportServlet implements Servlet
{
    private static final Logger logger = LoggerFactory.getLogger( AbstractAliasSupportServlet.class );

    private String servletInfo;
    private String alias;
    private ServletConfig servletConfig;
    private int basePathLength;
    

    private void calculateBasePathLength()
    {
        if ( basePathLength > 0 )
        {
            if ( logger.isDebugEnabled() )
                logger.debug( "basePathLength already set [{}], ignore calculation", basePathLength );
        }
        else
        {
            Assert.notNull( getAlias() );
            Assert.notNull( servletConfig );
            String servletAliasContextPath = HttpUtil.retriveContextPathFromServletAlias( getAlias() );
            String contextPath = servletConfig.getServletContext().getContextPath();
            Assert.notNull( contextPath );
            basePathLength = contextPath.length() + servletAliasContextPath.length() + 1;
            if ( logger.isDebugEnabled() )
                logger.debug( "appBasePathLength [" + basePathLength + "] parsed from servletAlias [" + getAlias()
                    + "] contextPath [" + contextPath + "]" );
        }
    }


    public void destroy()
    {
        basePathLength = 0;
        if ( logger.isDebugEnabled() )
            logger.debug( "servlet [" + servletInfo + "] destroyed" );
    }


    public void init( ServletConfig servletConfig ) throws ServletException
    {
        this.servletConfig = servletConfig;
        calculateBasePathLength();
        if ( logger.isDebugEnabled() )
            logger.debug( "servlet " + servletInfo + " inited" );
    }


    public String getServletInfo()
    {
        return servletInfo;
    }


    public void setServletInfo( String servletInfo )
    {
        this.servletInfo = servletInfo;
    }


    public String getAlias()
    {
        return alias;
    }


    public void setAlias( String alias )
    {
        this.alias = alias;
    }


    public ServletConfig getServletConfig()
    {
        return servletConfig;
    }


    public void setServletConfig( ServletConfig servletConfig )
    {
        this.servletConfig = servletConfig;
    }


    public int getBasePathLength()
    {
        return basePathLength;
    }


    public void setBasePathLength( int basePathLength )
    {
        this.basePathLength = basePathLength;
    }

}
