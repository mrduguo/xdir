package org.duguo.xdir.jcr.pool;

import java.io.File;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.commons.pool.KeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.jcr.pool.CredentialsWorkspaceKey;
import org.duguo.xdir.jcr.pool.PooledSession;
import org.duguo.xdir.jcr.utils.FileUtils;
import org.duguo.xdir.jcr.utils.JcrImportUtils;

public class GenericPooledRepository implements Repository{
    private static final Logger logger=LoggerFactory.getLogger(GenericPooledRepository.class);
    
	private Repository repository;
	private KeyedObjectPool pool;
	private File repositoryBase;
	private String defaultWorkspace="default";
	private Credentials defaultCredentials;
    
    public void initRepository() throws Exception  {
        File workspacesFolder=new File( repositoryBase, "workspaces");
        if(!workspacesFolder.exists()){
            FileUtils.copyDirectory( new File( repositoryBase, "../../template"), repositoryBase, false );
            Session session=null;
            try {
                session = login();
                File initBase=new File( repositoryBase,"init" );
                if(initBase.exists()){
                    long startTimestamp=System.currentTimeMillis();
                    JcrImportUtils.importFolder( session.getRootNode(), initBase );
                    logger.info( "import ["+initBase.getPath()+"] finished in "+(System.currentTimeMillis()-startTimestamp)+" milliseconds" );
                }
            }catch(RuntimeException ex){
                throw ex;                
            }catch(Exception ex){
                throw new RuntimeException("failed to init repository ["+repositoryBase.getPath()+"]",ex);                
            }finally{
                if(session!=null){
                    session.logout();
                }
            }
        }
    }
    
    public void destroyRepository() throws Exception  {
        KeyedObjectPool poolToDestroy=pool;
        pool=null;
        poolToDestroy.clear();
        if(logger.isDebugEnabled())
            logger.debug( "repository [{}] pool cleared", repositoryBase.getPath());
        onDestroy(repository);
    }
    
    public void onDestroy(Repository repository) throws Exception  {
        // leave for vendor specified destroy
    }
	
	// pooled session support
    public Session login(Credentials credentials, String workspace) throws LoginException,
            NoSuchWorkspaceException, RepositoryException {
        if(pool==null){
            // the repository already been destroyed.
            return null;
        }
        
        try {
            CredentialsWorkspaceKey key=new CredentialsWorkspaceKey(credentials,workspace);
            Session session=(Session)pool.borrowObject(key);
            return new PooledSession(session,pool,key);
        } catch (LoginException e) {
            throw e;
        } catch (NoSuchWorkspaceException e) {
            throw e;
        } catch (RepositoryException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    // we always using defaultCredentials if no credentials passed
    public Session login() throws LoginException, RepositoryException {
        return login(defaultCredentials,defaultWorkspace);
    }

    public Session login(Credentials credentials) throws LoginException,
            RepositoryException {
        return login(credentials,defaultWorkspace);
    }

    public Session login(String workspace) throws LoginException,
            NoSuchWorkspaceException, RepositoryException {
        return login(defaultCredentials,workspace);
    }

    // getters and setters   
    public Repository getRepository( )
    {
        return repository;
    } 
    public void setRepository( Repository repository )
    {
        this.repository = repository;
    }
    public void setPool( KeyedObjectPool pool )
    {
        this.pool = pool;
    }
    public void setRepositoryBase( File repositoryBase )
    {
        this.repositoryBase = repositoryBase;
    }
    public void setDefaultCredentials( Credentials defaultCredentials )
    {
        this.defaultCredentials = defaultCredentials;
    }
    
    // delegate methods to repository
    public String getDescriptor( String key )
    {
        return repository.getDescriptor( key );
    }
    public String[] getDescriptorKeys()
    {
        return repository.getDescriptorKeys();
    }
    public Value getDescriptorValue( String key )
    {
        return repository.getDescriptorValue( key );
    }
    public Value[] getDescriptorValues( String key )
    {
        return repository.getDescriptorValues( key );
    }
    public boolean isSingleValueDescriptor( String key )
    {
        return repository.isSingleValueDescriptor( key );
    }
    public boolean isStandardDescriptor( String key )
    {
        return repository.isStandardDescriptor( key );
    }

    public void setDefaultWorkspace( String defaultWorkspace )
    {
        this.defaultWorkspace = defaultWorkspace;
    }

    public KeyedObjectPool getPool()
    {
        return pool;
    }


}
