package org.duguo.xdir.jcr.pool;

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Repository;
import javax.jcr.Session;

public class PoolableSessionObjectFactory extends
        BaseKeyedPoolableObjectFactory {
    
    private static final Logger logger=LoggerFactory.getLogger(PoolableSessionObjectFactory.class);
    
    private Repository repository;

    public Object makeObject(Object key) throws Exception {
        CredentialsWorkspaceKey args=(CredentialsWorkspaceKey)key;
        Session session=repository.login(args.getCredentials(),args.getWorkspace());
        if(logger.isDebugEnabled())
            logger.debug("created new session for key {}",args.hashCode());
        return session;
    }
    
    public void destroyObject(Object key,Object value) throws Exception{
        try{
            ((Session)value).logout();
            if(logger.isDebugEnabled())
                logger.debug( "destroyed session" );
        }catch(Exception ex){
            if(logger.isDebugEnabled())
                logger.debug("failed to logout session, you may see this message during shutdown stage: "+ex.getMessage(),ex);
        }
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

}
