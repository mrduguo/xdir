package org.duguo.xdir.jcr.jackrabbit;


import java.io.File;
import java.util.Map;

import javax.jcr.Repository;

import org.springframework.util.Assert;
import org.duguo.xdir.jcr.factory.RepositoryAction;


public abstract class AbstractJackrabbitAction  implements RepositoryAction
{
    
    private RepositoriesHolder repositoriesHolder;

    @SuppressWarnings("unchecked")
    public String getRepositoryName( Map parameters ) throws Exception
    {
        String repositoryName=(String)parameters.get( "repositoryName" );
        Assert.notNull( repositoryName,"repositoryName is required");
        return repositoryName;
    }

    @SuppressWarnings("unchecked")
    public String getWorkspaceName( Map parameters ) throws Exception
    {
        String workspaceName=(String)parameters.get( "workspaceName" );
        Assert.notNull( workspaceName,"workspaceName is required");
        return workspaceName;
    }
    
    public Repository getRepository(String repositoryName) throws Exception
    {
        Repository repository=repositoriesHolder.getActiveRepositories().get( repositoryName );
        Assert.notNull( repository,"repository is required");
        return repository;
    }
    
    public File getRepositoryFolder(String repositoryName) throws Exception
    {
        File repositoryFolder = new File( repositoriesHolder.getRepositoriesBase() + "/" + repositoryName );
        return repositoryFolder;
    }

    public RepositoriesHolder getRepositoriesHolder()
    {
        return repositoriesHolder;
    }

    public void setRepositoriesHolder( RepositoriesHolder repositoriesHolder )
    {
        this.repositoriesHolder = repositoriesHolder;
    }


}
