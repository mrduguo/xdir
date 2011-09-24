package org.duguo.xdir.jcr.jackrabbit;


import java.io.File;
import java.util.Map;

import javax.jcr.Repository;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WorkspaceDeleteAction extends AbstractJackrabbitAction
{

    private static final Logger logger = LoggerFactory.getLogger( WorkspaceDeleteAction.class );

    private RepositoryShutdownAction repositoryShutdownAction;


    @SuppressWarnings("unchecked")
    public Repository execute( Map parameters ) throws Exception
    {
        String repositoryName = getRepositoryName( parameters );
        String workspaceName = getWorkspaceName( parameters );
        File repositoryFolder = new File( getRepositoriesHolder().getRepositoriesBase() + "/" + repositoryName );
        File workspaceFolder = new File( repositoryFolder, "workspaces/" + workspaceName );

        if ( workspaceFolder.exists() )
        {
            deleteWorkspace( parameters,repositoryName, workspaceFolder );
        }
        else
        {
            logger.warn( "ingore none exist workspace [" + workspaceName + "]", workspaceFolder.getPath() );
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    protected void deleteWorkspace( Map parameters,String repositoryName, File workspaceFolder ) throws Exception
    {
        if(getRepositoriesHolder().getDisposableRepositories().containsKey( repositoryName )){
            parameters.put( "action", "repositoryShutdown" );
            repositoryShutdownAction.execute( parameters );
        }
        FileUtils.deleteDirectory( workspaceFolder );
        logger.info( "repository workspace [{}] deleted",workspaceFolder.getPath() );
    }

    public void setRepositoryShutdownAction( RepositoryShutdownAction repositoryShutdownAction )
    {
        this.repositoryShutdownAction = repositoryShutdownAction;
    }

}
