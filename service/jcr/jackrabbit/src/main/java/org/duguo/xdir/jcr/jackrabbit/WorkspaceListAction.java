package org.duguo.xdir.jcr.jackrabbit;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Repository;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WorkspaceListAction extends AbstractJackrabbitAction
{

    private static final Logger logger = LoggerFactory.getLogger( WorkspaceListAction.class );
    
    private RepositoryLoadAction repositoryLoadAction;

    @SuppressWarnings("unchecked")
    public Repository execute( Map parameters ) throws Exception
    {
        listWorkspace( parameters);
        if(logger.isDebugEnabled())
            logger.debug( "listed workspaces" );
        return null;
    }

    @SuppressWarnings("unchecked")
    protected void listWorkspace(Map parameters) throws Exception
    {
        String repositoryName=getRepositoryName( parameters );
        if(!getRepositoriesHolder().getActiveRepositories().containsKey( repositoryName )){
            parameters.remove( "action" );
            repositoryLoadAction.execute( parameters );            
        }
        
        Map workspaces=new HashMap();
        parameters.put( "workspaces", workspaces );
        File workspacesFolder = new File( getRepositoriesHolder().getRepositoriesBase(),repositoryName+"/workspaces");
        if(workspacesFolder.exists() && workspacesFolder.isDirectory()){
            // add local workspaces
            for(String workspaceName:workspacesFolder.list()){
                File workspaceFolder = new File( workspacesFolder, workspaceName );
                if(workspaceFolder.isDirectory()){
                    addWorkspaceObject(workspaces,workspaceName,true);
                }
            }
        }else{
            // add none local repository
            parameters.remove( "action" );
            Repository repository=repositoryLoadAction.execute( parameters );
            Session session=repository.login();
            try{
                addWorkspaceObject(workspaces,session.getWorkspace().getName(),false);
            }finally{
                session.logout();
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void addWorkspaceObject( Map workspaces, String workspaceName, boolean isLocalWorkspace )
    {
        Map workspace=new HashMap();
        workspace.put( "isLocalWorkspace", isLocalWorkspace );
        workspaces.put( workspaceName, workspace);
    }

    public void setRepositoryLoadAction( RepositoryLoadAction repositoryLoadAction )
    {
        this.repositoryLoadAction = repositoryLoadAction;
    }

}
