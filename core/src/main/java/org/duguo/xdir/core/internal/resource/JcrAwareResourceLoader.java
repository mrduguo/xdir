package org.duguo.xdir.core.internal.resource;


import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.spi.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;


public class JcrAwareResourceLoader implements ResourceLoader
{

    private static final Logger logger = LoggerFactory.getLogger( JcrAwareResourceLoader.class );

    private static String PARENT_PREFIX="parent:";

    public Resource loadResource( ModelImpl model, String resource ) throws Exception
    {
        Resource templateResource=loadFromAllPaths( model, resource);
        return templateResource;
    }

    public String load( ModelImpl model, String templateName )
    {
        String templateString = load( model, templateName,Model.NULL_STRING);
        return templateString;
    }


    public String load( ModelImpl model, String templateName , String nodeType)
    {
        String templateString = load( model, templateName, nodeType, model.getFormat() );
        if ( templateString == null && nodeType != Model.NULL_STRING )
        {
            templateString = load( model, templateName, Model.NULL_STRING, model.getFormat() );
        }
        return templateString;
    }


    public String load( ModelImpl model, String templateName,String nodeType, String format )
    {
        String[] formatList=model.getApp().getFormats().get( format );
        String templateString = null;
        if(formatList==null){
            templateString = loadFromApplicationResource( model, templateName,nodeType, format);
        }else{
            for(String currentFormat:formatList){
                templateString = loadFromApplicationResource( model, templateName,nodeType, currentFormat);
                if ( templateString != null )
                {
                    break;
                }
            }
        }
        return templateString;
    }


    protected String loadFromApplicationResource( ModelImpl model, String templateName,String nodeType, String format )
    {
        String templateString = null;
        StringBuffer templatePath = new StringBuffer();
        if ( nodeType != Model.NULL_STRING)
        {
            templatePath.append( nodeType );
            templatePath.append( "/" );
        }
        templatePath.append( templateName );
        try{
            if(templateName.indexOf( '.' )>0){
                Resource templateResource=loadFromAllPaths( model, templatePath.toString() );
                if(templateResource!=null){
                    templateString= templateResource.getAsString();
                    return templateString;
                }
            }
            templatePath.append( format );
            Resource templateResource=loadFromAllPaths( model, templatePath.toString() );
            if(templateResource!=null){
                templateString= templateResource.getAsString();
            }
        }catch(Exception ex){
            logger.error( "failed to load template resource ["+templatePath+"]",ex );
        }
        return templateString;
    }

    protected Resource loadFromAllPaths( ModelImpl model, String resourcePath )throws Exception
    {
        int skipToParent=0;
        int parentIndex=resourcePath.indexOf( PARENT_PREFIX );
        while(parentIndex>=0){
            skipToParent++;
            resourcePath=resourcePath.substring(0,parentIndex)+resourcePath.substring(parentIndex+PARENT_PREFIX.length());
            parentIndex=resourcePath.indexOf( PARENT_PREFIX );
        }
        for (String searchPath:model.getApp().getTemplatePaths())
        {
            if(skipToParent>0){
                skipToParent--;
                continue;
            }
            String absPath = searchPath + resourcePath;
            Resource resource = loadResourceFromPath( model, absPath );   
            if ( resource != null )
            {
                return resource;
            }
        }
        return null;
    }


    protected Resource loadResourceFromPath( ModelImpl model, String absPath ) throws Exception
    {
        Resource resource = null;
        if ( model.getSession().itemExists( absPath ) )
        {
            Node resourceNode = model.getSession().getNode( absPath + "/jcr:content" );
            resource = new JcrResource( resourceNode.getProperty( "jcr:data" ));
            if ( logger.isDebugEnabled() )
                logger.debug( "template loaded from jcr path [" + absPath + "]" );
        }else{
            if(logger.isTraceEnabled())
                logger.trace( "template not found at [{}]",absPath);
        }
        return resource;
    }

}
