package org.duguo.xdir.jcr.jackrabbit;


import java.io.File;
import java.util.Map;

import javax.jcr.Repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RepositoryCreateAction extends RepositoryUpdateAction
{
    
    private static final Logger logger = LoggerFactory.getLogger( RepositoryCreateAction.class );

    @SuppressWarnings("unchecked")
    public Repository execute( Map parameters ) throws Exception
    {
        String repositoryName=getRepositoryName( parameters );
        File repositoryFolder = getRepositoryFolder( repositoryName );
        if(getRepositoriesHolder().getActiveRepositories().containsKey( repositoryName )){
            ignoreExistRepository(repositoryName);
        }else{
            createRepository(parameters, repositoryName,repositoryFolder );
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected void createRepository(Map parameters, String repositoryName, File repositoryFolder) throws Exception
    {
        File workspacesFolder=new File( repositoryFolder, "workspaces");
        if (!workspacesFolder.exists() )
        {
            if(!repositoryFolder.exists()){
                repositoryFolder.mkdirs();
            }
            updateRepositoryConfig(parameters,repositoryFolder);
            logger.info( "repository [" + repositoryName + "] created" );
        }else{
            ignoreExistRepository(repositoryName);
        }
    }


    protected void ignoreExistRepository( String repositoryName )
    {
        logger.warn("ignore exist repository [{}]",repositoryName);
    }

}
