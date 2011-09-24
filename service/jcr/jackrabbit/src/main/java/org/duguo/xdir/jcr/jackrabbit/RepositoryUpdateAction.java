package org.duguo.xdir.jcr.jackrabbit;


import java.io.File;
import java.util.Map;

import javax.jcr.Repository;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RepositoryUpdateAction extends RepositoryShutdownAction
{

    public static final String REPO_CONF_FILE_PREFIX = "repo_conf_file:";
    
    private static final Logger logger = LoggerFactory.getLogger( RepositoryUpdateAction.class );

    @SuppressWarnings("unchecked")
    public Repository execute( Map parameters ) throws Exception
    {
        String repositoryName=getRepositoryName( parameters );
        File repositoryFolder = getRepositoryFolder( repositoryName );
        updateRepositoryConfig(parameters,repositoryFolder);
        if(getRepositoriesHolder().getDisposableRepositories().containsKey( repositoryName )){
            Repository repository=getRepository( repositoryName );
            shutdownRepository( repositoryName, repository );
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected void updateRepositoryConfig( Map parameters, File repositoryFolder ) throws Exception
    {
        for(Object key:parameters.keySet()){
            String configFile=(String)key;
            if(configFile.startsWith( REPO_CONF_FILE_PREFIX )){
                String configValue=(String)parameters.get( key );
                if(configValue!=null && configValue.trim().length()>0){
                    configFile=configFile.substring( REPO_CONF_FILE_PREFIX.length() );
                    FileUtils.writeStringToFile( new File(repositoryFolder,configFile), configValue ); 
                    if(logger.isDebugEnabled())
                        logger.debug("updated repository config file [{}]",configFile);
                }
            }
        }
    }

}
