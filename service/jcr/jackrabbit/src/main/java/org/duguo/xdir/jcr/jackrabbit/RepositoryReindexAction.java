package org.duguo.xdir.jcr.jackrabbit;


import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.jcr.Repository;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


public class RepositoryReindexAction extends RepositoryShutdownAction
{

    private static final Logger logger = LoggerFactory.getLogger( RepositoryReindexAction.class );

    @SuppressWarnings("unchecked")
    public Repository execute( Map parameters ) throws Exception
    {
        String repositoryName=getRepositoryName( parameters );
        if(getRepositoriesHolder().getDisposableRepositories().containsKey( repositoryName )){
            Repository repository=getRepository( repositoryName );
            shutdownRepository( repositoryName,repository );
        }
        Assert.state(!getRepositoriesHolder().getActiveRepositories().containsKey( repositoryName ) ,"doesn't support reindex none jackrabbit repository" );
                
        deleteIndex( repositoryName );
        return null;
    }

    protected void deleteIndex( String repositoryName ) throws Exception, IOException
    {
        File repositoryFolder = getRepositoryFolder( repositoryName );
        if(repositoryFolder.exists()){
            // delete repository index
            File repositoryIndexFolder=new File(repositoryFolder,"repository/index");
            deleteIndexFolderIfExist(  repositoryIndexFolder );
            
            // delete workspaces indexes
            File repositoryWorkspacesFolder=new File(repositoryFolder,"workspaces");
            if(repositoryWorkspacesFolder.exists()){
                for(String workspaceName:repositoryWorkspacesFolder.list()){
                    File workspaceIndexFolder=new File(repositoryWorkspacesFolder,workspaceName+"/index");
                    deleteIndexFolderIfExist(  workspaceIndexFolder );
                }
            }
            
        }else{
            logger.warn("delete index of none exist repository [{}]",repositoryName);
        }
    }

    private void deleteIndexFolderIfExist(File repositoryIndexFolder ) throws IOException
    {
        if(repositoryIndexFolder.exists()){
            FileUtils.deleteDirectory( repositoryIndexFolder );        
            if(logger.isDebugEnabled())
                logger.debug("deleted repository index [{}]",repositoryIndexFolder.getPath());        
        }
    }

}
