package org.duguo.xdir.jcr.jackrabbit;


import java.io.File;
import java.util.Map;

import javax.jcr.Credentials;
import javax.jcr.Repository;

import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.jackrabbit.core.TransientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.Assert;
import org.duguo.xdir.jcr.pool.PoolableSessionObjectFactory;


public class RepositoryLoadAction extends AbstractJackrabbitAction implements BeanFactoryAware,DisposableBean
{

    private static final Logger logger = LoggerFactory.getLogger( RepositoryLoadAction.class );

    
    private boolean autoCreateRepository = false;
    private Credentials defaultCredentials;
    private String poolBeanName;
    
    private BeanFactory beanFactory;

    @SuppressWarnings("unchecked")
    public Repository execute( Map parameters ) throws Exception
    {
        return retriveRepository(parameters);
    }

    public void destroy() throws Exception
    {
        for(Map.Entry<String, PooledRepository> entry:getRepositoriesHolder().getDisposableRepositories().entrySet()){
            entry.getValue().destroyRepository();
        }
    }

    @SuppressWarnings("unchecked")
    protected Repository loadRepository( Map parameters, String repositoryName, File repositoryFolder ) throws Exception
    {
        TransientRepository rawRepository = new TransientRepository( repositoryFolder );
        PoolableSessionObjectFactory sessionObjectFactory = new PoolableSessionObjectFactory();
        sessionObjectFactory.setRepository( rawRepository );

        GenericKeyedObjectPool pool = beanFactory.getBean( poolBeanName, GenericKeyedObjectPool.class );
        pool.setFactory( sessionObjectFactory );

        
        PooledRepository pooledRepository = new PooledRepository();
        pooledRepository.setPool( pool );
        pooledRepository.setRepository( rawRepository );
        pooledRepository.setRepositoryBase( repositoryFolder );
        pooledRepository.setDefaultCredentials( defaultCredentials );
        if(parameters.containsKey(  "defaultWorkspace" )){
            pooledRepository.setDefaultWorkspace( (String) parameters.get( "defaultWorkspace" ) );   
        }        
        pooledRepository.initRepository();

        getRepositoriesHolder().getActiveRepositories().put( repositoryName, pooledRepository );
        getRepositoriesHolder().getDisposableRepositories().put( repositoryName, pooledRepository );
        if ( logger.isDebugEnabled() )
            logger.debug( "new repository [" + repositoryName + "] created" );
        return pooledRepository;
    }


    @SuppressWarnings("unchecked")
    protected synchronized Repository retriveRepository( Map parameters) throws Exception
    {
        String repositoryName =getRepositoryName( parameters );
        
        File repositoryFolder = getRepositoryFolder( repositoryName );
        parameters.put( "repositoryFolder", repositoryFolder.getPath() );
        
        Repository repository = getRepositoriesHolder().getActiveRepositories().get( repositoryName );
        if ( logger.isDebugEnabled() )
            logger.debug( "getRepository(" + repositoryName + ")" );
        if ( repository != null )
        {
            if ( logger.isDebugEnabled() )
                logger.debug( "getRepository(" + repositoryName + ") found in cached repositories" );
            return repository;
        }
        return loadNewRepository( parameters, repositoryName, repositoryFolder );
    }

    @SuppressWarnings("unchecked")
    protected Repository loadNewRepository( Map parameters, String repositoryName, File repositoryFolder )
        throws Exception
    {
        
        if ( logger.isDebugEnabled() )
            logger.debug( "create new repository" );

        if ( !repositoryFolder.exists() && !autoCreateRepository )
        {
            Assert.state(false, "cannot load none exist repository: repository not exist and autoCreateRepository is false" );
        }
        
        return loadRepository( parameters, repositoryName, repositoryFolder );
    }

    public void setPoolBeanName( String poolBeanName )
    {
        this.poolBeanName = poolBeanName;
    }

    public void setBeanFactory( BeanFactory beanFactory ) throws BeansException
    {
        this.beanFactory=beanFactory;
    }

    public void setAutoCreateRepository( boolean autoCreateRepository )
    {
        this.autoCreateRepository = autoCreateRepository;
    }

    public void setDefaultCredentials( Credentials defaultCredentials )
    {
        this.defaultCredentials = defaultCredentials;
    }

}
