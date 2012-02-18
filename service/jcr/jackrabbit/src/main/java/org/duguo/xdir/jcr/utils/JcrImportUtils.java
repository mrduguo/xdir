package org.duguo.xdir.jcr.utils;


import java.io.*;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.*;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.commons.io.FileUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JcrImportUtils
{
    
    public static final String PROPERTIES_FILE_PREFIX="jcr.properties";
    public static final String JCR_XML_FILE_SUFFIX="-jcr.xml";

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
                }else if(childFile.isFile() && fileName.endsWith(JCR_XML_FILE_SUFFIX)){
                    fileName=fileName.substring(0,fileName.length()-JCR_XML_FILE_SUFFIX.length());
                    if(!parentNode.hasNode( fileName )){
                        importJcrSystemViewXml(parentNode, childFile);
                    }
                }else if ( !parentNode.hasNode( fileName ) ){
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
                    JcrNodeUtils.setNodeProperty(parentNode,entry.getKey(), entry.getValue() );
                }
            }
            if ( parentNode.getSession().hasPendingChanges() )
            {
                parentNode.getSession().save();
            }
        }
    }

    private static void importJcrSystemViewXml(Node parentNode, File xmlFile) throws Exception{
        InputStream fileInputStream=new FileInputStream(xmlFile);
        parentNode.getSession().importXML(parentNode.getPath(), fileInputStream, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
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
                    Utf8SortedProperties utf8SortedProperties=new Utf8SortedProperties();
                    utf8SortedProperties.load(new FileInputStream(nodeBaseFolder.getPath() + "/" + fileName));
                    properties.putAll( utf8SortedProperties );
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
        String value= FileUtils.readFileToString(new File(parentFolder,fileName),"utf-8");
        properties.put( key, value );
    }
}
