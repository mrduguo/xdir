package org.duguo.xdir.jcr.jackrabbit;

import org.apache.jackrabbit.core.TransientRepository;
import org.duguo.xdir.jcr.pool.GenericPooledRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Repository;

public class PooledRepository extends GenericPooledRepository{
    
    private static final Logger logger=LoggerFactory.getLogger(PooledRepository.class);
        
    public void onDestroy(Repository repository) throws Exception  {
        if(repository instanceof TransientRepository){
            ((TransientRepository)repository).shutdown();
            if(logger.isDebugEnabled())
                logger.debug( "repository shutdown successfully");
        }
    }

}
