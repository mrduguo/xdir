package org.duguo.xdir.core.internal.servlet;


import javax.servlet.Servlet;

import org.duguo.xdir.core.internal.app.Application;


public interface XdirServlet  extends Servlet
{

    public Application getApplication();

    public void setApplication( Application application );
    
    public void setServletInfo( String servletInfo );


    public String getAlias();


    public void setAlias( String alias );


    public int getBasePathLength();


    public void setBasePathLength( int basePathLength );

}
