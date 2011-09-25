package org.duguo.xdir.core.internal.resource;


import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.core.internal.model.ModelImpl;

public class DirectoryAwareResourceLoader extends JcrAwareResourceLoader {
    
    private static final Logger logger = LoggerFactory.getLogger( DirectoryAwareResourceLoader.class );
    private String resourceBase;
    
    protected Resource loadResourceFromPath( ModelImpl model, String resourcePath )throws Exception
    {
        Resource resource=loadResourceFromDirectory( model, resourcePath );
        if(resource==null){
            resource=super.loadResourceFromPath( model, resourcePath );
        }
        return resource;
    }

    protected Resource loadResourceFromDirectory( ModelImpl model, String absPath ) throws Exception
    {
        Resource resource=null;
        File resourceFile=new File( resourceBase+absPath );
        if(resourceFile.exists() && resourceFile.isFile()){
            resource=new FileResource( resourceFile );
            if(logger.isDebugEnabled())
                logger.debug( "template loaded from file path ["+resourceFile.getPath()+"]" );
        }
        return resource;
    }

    public void setResourceBase( String resourceBase )
    {
        this.resourceBase = resourceBase;
    }
}
