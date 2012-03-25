package org.duguo.xdir.core.internal.app;


import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.spi.model.support.AbstractGetAndPut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * Redirect request to a new location
 */
public class RedirectApplication extends AbstractGetAndPut implements Application
{
    private Logger logger = LoggerFactory.getLogger( RedirectApplication.class );

    private Map<String, Application> children=new HashMap<String, Application>();
    private Application parent;
    private String targetLocation;

    public int handle( ModelImpl model ) throws Exception
    {
        model.getResponse().sendRedirect(targetLocation);
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

    public void setTargetLocation( String targetLocation )
    {
        this.targetLocation=targetLocation;
    }

}
