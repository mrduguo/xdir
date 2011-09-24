package org.duguo.xdir.jcr.utils;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Binary;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JcrImportUtils
{
    
    private static final String PROPERTIES_FILE_PREFIX="_properties";

    private static final Logger logger=LoggerFactory.getLogger(JcrImportUtils.class);
    


    public static void importFolder( Node parentNode, File parentFolder ) throws Exception
    {
        if ( parentFolder.exists() && parentFolder.isDirectory() )
        {
            boolean hasPropertiesFile = false;
            for ( String fileName : parentFolder.list() )
            {
                File childFile = new File( parentFolder, fileName );
                if ( childFile.isFile() && fileName.startsWith(PROPERTIES_FILE_PREFIX) )
                {
                    hasPropertiesFile=true;
                }
                else if ( !parentNode.hasNode( fileName ) )
                {
                    if ( childFile.isDirectory() )
                    {
                        Node childNode = parentNode.addNode( fileName,"nt:xunstructured" );
                        if(logger.isDebugEnabled())
                            logger.debug( "added node [{}]", childFile.getPath());
                        importFolder( childNode, childFile );
                    }
                    else
                    {
                        addFileNode( parentNode, childFile );
                    }
                }
            }
            if ( hasPropertiesFile)
            {
                Map<String, String> nodeProperties = loadPropertiesFromFile( parentFolder );
                for ( Map.Entry<String, String> entry : nodeProperties.entrySet() )
                {
                    parentNode.setProperty( entry.getKey(), entry.getValue() );
                }
            }
            if ( parentNode.getSession().hasPendingChanges() )
            {
                parentNode.getSession().save();
            }
        }
    }


    public static void addFileNode( Node parentNode, File childFile ) throws ItemExistsException,
        PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException,
        RepositoryException
    {
        Node childNode = parentNode.addNode( childFile.getName(), "nt:xfile" );
        Node contentNode = childNode.addNode( "jcr:content");
        FileInputStream fileInputStream = null;
        try
        {
            fileInputStream = new FileInputStream( childFile );
            Binary binary = childNode.getSession().getValueFactory().createBinary( fileInputStream );
            contentNode.setProperty( "jcr:data", binary );
        }
        catch ( Exception e )
        {
            logger.error( "failed to attach file",e );
        }
        finally
        {
            try
            {
                fileInputStream.close();
                if(logger.isDebugEnabled())
                    logger.debug( "attached file [{}]", childFile.getName());
            }
            catch ( IOException e )
            {
                logger.error( "failed to attach file",e );
            }
        }
    }


    private static Map<String, String> loadPropertiesFromFile( File nodeBaseFolder ) throws Exception
    {
        Map<String, String> properties=new HashMap<String, String>();
        for(String fileName:nodeBaseFolder.list()){
            if(fileName.startsWith( PROPERTIES_FILE_PREFIX )){
                if(fileName.equals( PROPERTIES_FILE_PREFIX )){
                    Map<String, String> tempProperties=MessagesLoader.load( nodeBaseFolder.getPath()+"/"+fileName );
                    properties.putAll( tempProperties );
                }else{
                    loadSingePropertyFromFile(properties,nodeBaseFolder,fileName);
                }
            }
        }
        return properties;
    }


    private static void loadSingePropertyFromFile( Map<String, String> properties, File parentFolder, String fileName ) throws Exception
    {
        String key=fileName.substring( PROPERTIES_FILE_PREFIX.length() );
        String value=readFileAsString(new File(parentFolder,fileName));
        properties.put( key, value );
    }
    

    
    private static String readFileAsString( File sourceFile ) throws IOException
    {
        if ( sourceFile.exists() && sourceFile.isFile() && sourceFile.length() > 0 )
        {
            BufferedReader input = new BufferedReader( new FileReader( sourceFile ) );
            try
            {
                StringBuilder fileString = new StringBuilder();
                String textFromFile = null;
                while((textFromFile = input.readLine())!=null){
                    fileString.append( textFromFile );
                    fileString.append( '\n' );
                }
                return fileString.toString();
            }
            finally
            {
                input.close();
            }
        }
        return null;
    }
}
