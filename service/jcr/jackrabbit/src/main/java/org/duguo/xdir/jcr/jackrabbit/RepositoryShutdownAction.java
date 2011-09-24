package org.duguo.xdir.jcr.jackrabbit;


import java.util.Map;

import javax.jcr.Repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


public class RepositoryShutdownAction extends AbstractJackrabbitAction
{

    private static final Logger logger = LoggerFactory.getLogger( RepositoryShutdownAction.class );

    @SuppressWarnings("unchecked")
    public Repository execute( Map parameters ) throws Exception
    {
        String repositoryName=getRepositoryName( parameters );
        if(getRepositoriesHolder().getDisposableRepositories().containsKey( repositoryName )){
            Repository repository=getRepository( repositoryName );
            shutdownRepository( repositoryName, repository );
        }else{
            if(getRepositoriesHolder().getActiveRepositories().containsKey( repositoryName )){
                logger.warn("shutdown [{}] ignored for remove repository [{}]",repositoryName);                   
            }else{
                logger.warn("shutdown [{}] ignored for none active repository [{}]",repositoryName);   
            }         
        }
        return null;
    }

    protected void shutdownRepository(String repositoryName,Repository repository) throws Exception
    {
        Assert.notNull( repository,"repository not exist" );
        Assert.state( getRepositoriesHolder().getDisposableRepositories().containsKey( repositoryName ),"disposable reposiotry not exist" );
        getRepositoriesHolder().getDisposableRepositories().remove( repositoryName );
        getRepositoriesHolder().getActiveRepositories().remove( repositoryName );
        
        if(repository instanceof PooledRepository){
            ((PooledRepository)repository).destroyRepository();
            if(logger.isDebugEnabled())
                logger.debug("shutdown repository [{}] success",repositoryName);
        }else{
            logger.warn("shutdown [{}] ignored for unknown repository [{}]",repositoryName,repository.getClass().getName());            
        }
    }

}
