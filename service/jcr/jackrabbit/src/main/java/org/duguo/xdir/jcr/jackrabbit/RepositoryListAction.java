package org.duguo.xdir.jcr.jackrabbit;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


public class RepositoryListAction extends AbstractJackrabbitAction
{

    private static final Logger logger = LoggerFactory.getLogger( RepositoryListAction.class );

    @SuppressWarnings("unchecked")
    public Repository execute( Map parameters ) throws Exception
    {
        listRepository( parameters);
        if(logger.isDebugEnabled())
            logger.debug( "listed repositories" );
        return null;
    }

    @SuppressWarnings("unchecked")
    protected void listRepository(Map parameters) throws Exception
    {
        Map repositories=new HashMap();
        parameters.put( "repositories", repositories );
        File templateFolder = new File( getRepositoriesHolder().getRepositoriesBase(), "../template");
        parameters.put( "repositoryTemplateFolder", templateFolder.getPath() );
        
        File repositoriesFolder = new File( getRepositoriesHolder().getRepositoriesBase());        
        if(!repositoriesFolder.exists() || !repositoriesFolder.isDirectory()){
            Assert.state( false,"invalid repositories base ["+ repositoriesFolder.getPath()+"]");
        }
        
        // add local repository
        for(String repositoryName:repositoriesFolder.list()){
            File repositoryFolder = new File( repositoriesFolder, repositoryName );
            if(repositoryFolder.isDirectory()){
                addRepositoryObject(repositories,repositoryName,true,repositoryFolder.getPath());
            }
        }
        
        // add none local repository
        for(String repositoryName:getRepositoriesHolder().getActiveRepositories().keySet()){
            if(!repositories.containsKey( repositoryName )){
                addRepositoryObject(repositories,repositoryName,false,null);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void addRepositoryObject( Map repositories, String repositoryName, boolean isLocalRepository,String repositoryBase )
    {
        Map repository=new HashMap();
        repository.put( "isLocalRepository", isLocalRepository );
        repository.put( "repositoryBase", repositoryBase );
        repository.put( "isActive", getRepositoriesHolder().getActiveRepositories().containsKey( repositoryName ) );
        repositories.put( repositoryName, repository);
    }

}
