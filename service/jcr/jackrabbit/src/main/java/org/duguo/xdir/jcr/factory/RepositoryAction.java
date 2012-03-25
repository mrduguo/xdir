package org.duguo.xdir.jcr.factory;

import javax.jcr.Repository;
import java.util.Map;

public interface RepositoryAction
{
   
    @SuppressWarnings("unchecked")
    public Repository execute(Map parameters) throws Exception;
    
}
