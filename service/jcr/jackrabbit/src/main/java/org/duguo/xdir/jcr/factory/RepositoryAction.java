package org.duguo.xdir.jcr.factory;

import java.util.Map;

import javax.jcr.Repository;

public interface RepositoryAction
{
   
    @SuppressWarnings("unchecked")
    public Repository execute(Map parameters) throws Exception;
    
}
