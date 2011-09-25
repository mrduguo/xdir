package org.duguo.xdir.core.internal.server;

public interface OsgiFrameworkAdaptor
{
    public void restart();
    
    public String hotDeployFiles(String[] bundleFiles) throws Exception;
    
}
