package org.duguo.xdir.jcr.jackrabbit;


import java.util.Map;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.data.GarbageCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.duguo.xdir.jcr.pool.PooledSession;


public class RepositoryCleanAction extends AbstractJackrabbitAction
{

    private static final Logger logger = LoggerFactory.getLogger( RepositoryCleanAction.class );

    private RepositoryLoadAction repositoryLoadAction;

    @SuppressWarnings("unchecked")
    public Repository execute( Map parameters ) throws Exception
    {
        String repositoryName = getRepositoryName( parameters );
        parameters.remove( "action" );
        Session session=null;
        try{            
            Repository repository = repositoryLoadAction.execute( parameters );
            session = repository.login();
            if ( session == null )
            {
                session = repositoryLoadAction.execute( parameters ).login();
                Assert.notNull( session );
            }
            Session realSession=null;
            if(session instanceof PooledSession){
                PooledSession pooledSession=(PooledSession)session;
                realSession=pooledSession.getSession();
            }
            if(realSession instanceof SessionImpl){
                SessionImpl sessionImpl=(SessionImpl)realSession;
                cleanDataStore( repositoryName, sessionImpl );
            }
        }finally{
            session.logout();
        }
        return null;
    }


    protected void cleanDataStore( String repositoryName, SessionImpl sessionImpl ) throws RepositoryException
    {
        System.gc();
        System.gc();
        GarbageCollector gc = sessionImpl.createDataStoreGarbageCollector();
        gc.mark();
        int cleanedNumbeers=gc.sweep();
        logger.info( "repository [{}] cleaned [{}] items",repositoryName,cleanedNumbeers);
    }

    public void setRepositoryLoadAction( RepositoryLoadAction repositoryLoadAction )
    {
        this.repositoryLoadAction = repositoryLoadAction;
    }

}
