package org.duguo.xdir.jcr.jackrabbit;


import java.io.File;
import java.util.Map;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.Session;

import org.apache.commons.io.FileUtils;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.jackrabbit.core.TransientRepository;
import org.duguo.xdir.jcr.utils.JcrImportUtils;
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
    private File repositoryFolder;
    private PooledRepository repository;
    
    private BeanFactory beanFactory;

    @SuppressWarnings("unchecked")
    public Repository execute( Map parameters ) throws Exception
    {
        return retriveRepository(parameters);
    }

    public void destroy() throws Exception
    {
        repository.destroyRepository();
    }

    @SuppressWarnings("unchecked")
    protected void loadRepository( Map parameters) throws Exception
    {
        boolean isNewRepo=!repositoryFolder.exists();
        if(isNewRepo){
            File repoConfFolder=new File(repositoryFolder.getParentFile(),"init/conf");
            FileUtils.copyDirectory(repoConfFolder,repositoryFolder);
        }
        TransientRepository rawRepository = new TransientRepository( repositoryFolder );
        PoolableSessionObjectFactory sessionObjectFactory = new PoolableSessionObjectFactory();
        sessionObjectFactory.setRepository( rawRepository );

        GenericKeyedObjectPool pool = beanFactory.getBean( poolBeanName, GenericKeyedObjectPool.class );
        pool.setFactory( sessionObjectFactory );

        
        repository = new PooledRepository();
        repository.setPool(pool);
        repository.setRepository(rawRepository);
        repository.setRepositoryBase(repositoryFolder);
        repository.setDefaultCredentials(defaultCredentials);
        if(parameters.containsKey("defaultWorkspace")) {
            repository.setDefaultWorkspace((String) parameters.get("defaultWorkspace"));
        }
        if(isNewRepo){
            Session session=null;
            try {
                session = repository.login();
                File initBase=new File(repositoryFolder.getParentFile(),"init/import");
                if(initBase.exists()){
                    long startTimestamp=System.currentTimeMillis();
                    JcrImportUtils.importFolder(session.getRootNode(), initBase);
                    logger.info( "import ["+initBase.getPath()+"] finished in "+(System.currentTimeMillis()-startTimestamp)+" milliseconds" );
                }
            }catch(RuntimeException ex){
                throw ex;
            }catch(Exception ex){
                throw new RuntimeException("failed to init repository ["+repositoryFolder.getPath()+"]",ex);
            }finally{
                if(session!=null){
                    session.logout();
                }
            }
            if ( logger.isDebugEnabled() )
                logger.info( "repository [" + repositoryFolder.getAbsolutePath() + "] created" );
        }
    }


    @SuppressWarnings("unchecked")
    protected synchronized Repository retriveRepository( Map parameters) throws Exception
    {
        parameters.put( "repositoryFolder", repositoryFolder.getPath() );
        
        if ( repository == null )
        {
            loadRepository( parameters);
        }
        return repository;
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

    public void setRepositoryFolder(File repositoryFolder) {
        this.repositoryFolder = repositoryFolder;
    }
}
