package org.duguo.xdir.core.internal.app.register;


import org.duguo.xdir.core.internal.app.Application;


public interface ApplicationService
{

    public void register( String path, Application application ) throws Exception;


    public void unregister( String path ) throws Exception;

}