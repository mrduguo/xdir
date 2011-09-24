package org.duguo.xdir.jcr.jackrabbit;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Repository;

public class RepositoriesHolder
{

    private Map<String, PooledRepository> disposableRepositories = new HashMap<String, PooledRepository>();

    private Map<String, Repository> activeRepositories = new HashMap<String, Repository>();
    
    private String repositoriesBase;

    public Map<String, PooledRepository> getDisposableRepositories()
    {
        return disposableRepositories;
    }

    public void setDisposableRepositories( Map<String, PooledRepository> disposableRepositories )
    {
        this.disposableRepositories = disposableRepositories;
    }

    public Map<String, Repository> getActiveRepositories()
    {
        return activeRepositories;
    }

    public void setActiveRepositories( Map<String, Repository> activeRepositories )
    {
        this.activeRepositories = activeRepositories;
    }

    public String getRepositoriesBase()
    {
        return repositoriesBase;
    }

    public void setRepositoriesBase( String repositoriesBase )
    {
        this.repositoriesBase = repositoriesBase;
    }
    
}
