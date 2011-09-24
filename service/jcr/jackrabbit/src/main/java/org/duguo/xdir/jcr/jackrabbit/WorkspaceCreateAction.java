package org.duguo.xdir.jcr.jackrabbit;


import java.io.File;
import java.util.Map;

import javax.jcr.Repository;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


public class WorkspaceCreateAction extends AbstractJackrabbitAction
{

    private static final Logger logger = LoggerFactory.getLogger( WorkspaceCreateAction.class );

    private RepositoryLoadAction repositoryLoadAction;


    @SuppressWarnings("unchecked")
    public Repository execute( Map parameters ) throws Exception
    {
        String repositoryName = getRepositoryName( parameters );
        String workspaceName = getWorkspaceName( parameters );
        File repositoryFolder = new File( getRepositoriesHolder().getRepositoriesBase() + "/" + repositoryName );
        File workspaceFolder = new File( repositoryFolder, "workspaces/" + workspaceName );

        if ( workspaceFolder.exists() )
        {
            logger.warn( "ingore create exist workspace [" + workspaceName + "]", workspaceFolder.getPath() );
        }
        else
        {
            createWorkspace( parameters, workspaceName );
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    protected void createWorkspace( Map parameters, String workspaceName ) throws Exception
    {
        parameters.remove( "action" );
        String sourceWorkspaceName = ( String ) parameters.get( "sourceWorkspaceName" );
        Session session = null;
        try
        {
            if ( sourceWorkspaceName == null )
            {
                Repository repository = repositoryLoadAction.execute( parameters );
                session = repository.login();
                if ( session == null )
                {
                    session = repositoryLoadAction.execute( parameters ).login();
                    Assert.notNull( session );
                }
                session.getWorkspace().createWorkspace( workspaceName );
            }
            else
            {
                Repository repository = repositoryLoadAction.execute( parameters );
                session = repository.login( sourceWorkspaceName );
                if ( session == null )
                {
                    session = repositoryLoadAction.execute( parameters ).login( sourceWorkspaceName );
                    Assert.notNull( session );
                }
                session.getWorkspace().createWorkspace( workspaceName, sourceWorkspaceName );
            }
        }
        finally
        {
            if(session!=null){
                session.logout();
            }
        }
        logger.info( "repository workspace [" + workspaceName + "] created" );
    }

    public void setRepositoryLoadAction( RepositoryLoadAction repositoryLoadAction )
    {
        this.repositoryLoadAction = repositoryLoadAction;
    }

}
