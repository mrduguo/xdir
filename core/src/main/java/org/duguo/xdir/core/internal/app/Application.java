package org.duguo.xdir.core.internal.app;

import java.util.Map;

import org.duguo.xdir.core.internal.model.ModelImpl;
public interface Application
{
    public static final int STATUS_REGISGERED_CONTROLLER=1;
    public static final int STATUS_SUCCESS=0;
    public static final int STATUS_PAGE_NOT_FOUND=404;
    public static final int STATUS_INTERNAL_ERROR=500;
    
    public int handle( ModelImpl model ) throws Exception;
    public int execute( ModelImpl model ) throws Exception;
    public Map<String, Application> getChildren();
    public Application getParent();
    
}
