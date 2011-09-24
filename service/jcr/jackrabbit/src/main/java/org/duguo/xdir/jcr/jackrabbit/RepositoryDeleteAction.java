package org.duguo.xdir.jcr.jackrabbit;


import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.jcr.Repository;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


public class RepositoryDeleteAction extends RepositoryShutdownAction
{

    private static final Logger logger = LoggerFactory.getLogger( RepositoryDeleteAction.class );

    @SuppressWarnings("unchecked")
    public Repository execute( Map parameters ) throws Exception
    {
        String repositoryName=getRepositoryName( parameters );
        if(getRepositoriesHolder().getDisposableRepositories().containsKey( repositoryName )){
            Repository repository=getRepository( repositoryName );
            shutdownRepository( repositoryName,repository );
        }        
        Assert.state(!getRepositoriesHolder().getActiveRepositories().containsKey( repositoryName ) ,"doesn't support delete none jackrabbit repository" );
        
        deleteRepository( repositoryName );
        return null;
    }

    protected void deleteRepository( String repositoryName ) throws Exception, IOException
    {
        File repositoryFolder = getRepositoryFolder( repositoryName );
        if(repositoryFolder.exists()){
            FileUtils.deleteDirectory( repositoryFolder );
            if(logger.isDebugEnabled())
                logger.debug("deleted repository [{}]",repositoryName);
        }else{
            logger.warn("delete none exist repository [{}]",repositoryName);
        }
    }

}
