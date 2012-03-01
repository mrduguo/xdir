package org.duguo.xdir.core.internal.jcr;


import java.util.HashMap;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.duguo.xdir.core.internal.config.PropertiesService;
import org.duguo.xdir.core.internal.model.FormatService;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.core.internal.model.PathInfoImpl;
import org.duguo.xdir.jcr.utils.JcrNodeUtils;
import org.duguo.xdir.spi.util.bean.BeanUtil;


public class JcrFactoryImpl extends QueryFactoryImpl implements JcrFactory
{
    private static final Logger logger = LoggerFactory.getLogger( JcrFactoryImpl.class );

    private RepositoryFactory repositoryFactory;
    private Repository repository;
    private PropertiesService propertiesService;


    public void bindValueToObject(Node sourceNode,Object beanInstance,String bindPrefix) throws Exception{
        PropertyIterator nodeProperties = sourceNode.getProperties();
        while(nodeProperties.hasNext()){
            Property nodeProperty = nodeProperties.nextProperty();
            String propertyName=nodeProperty.getName();
            if(propertyName.startsWith( bindPrefix )){
                propertyName=propertyName.substring( bindPrefix.length() );
                propertyName=BeanUtil.underscoreNameToBeanName( propertyName );
                String propertyValue=JcrNodeUtils.getPropertyStringValue( nodeProperty );
                propertyValue=propertiesService.resolveStringValue( propertyValue );
                BeanUtil.bindFieldValueIfHasSetter(beanInstance,propertyName,propertyValue);
            }
        }
    }

    public Node retriveNode( ModelImpl model ) throws RepositoryException
    {
        String absPath = model.getPathInfo().getJcrPath(model.getApp().getJcrBasePath());      
        Node node = null;
        try
        {
            if ( logger.isDebugEnabled() )
                logger.debug( "retrive node with absPath [" + absPath + "]" );
            node = model.getSession().getNode( absPath );
            model.setNode( node );
            if ( logger.isDebugEnabled() )
                logger.debug( "node retrived with absPath [" + absPath + "]" );
        }
        catch ( PathNotFoundException ex )
        {
            // for file node such as /foo/bar/attachments/logo.jpg
            absPath=absPath+model.getFormat();
            if(model.getSession().itemExists( absPath )){
                node = model.getSession().getNode( absPath);
                model.setNode( node );
                model.setFormat( FormatService.FORMAT_FOLDER );
            }
        }
        return node;
    }

    public Node resolveBestNode( ModelImpl model ) throws RepositoryException
    {
        if(logger.isTraceEnabled()) logger.trace("> resolveBestNode {}",model.getPathInfo());
        PathInfoImpl pathInfo = model.getPathInfo();
        Node node=model.getSession().getNode(model.getApp().getJcrBasePath());
        while(pathInfo.getCurrentPath()!=null){
            if(node.hasNode( pathInfo.getCurrentPath())){
                node=node.getNode(pathInfo.getCurrentPath());
                pathInfo.moveToNextPath();
            }else{
                // for file node such as /foo/bar/attachments/logo.jpg
                String fileNode=pathInfo.getCurrentPath()+model.getFormat();
                if(node.hasNode(fileNode )){
                    node=node.getNode(fileNode);
                    pathInfo.moveToNextPath();
                    model.setFormat( FormatService.FORMAT_FOLDER );
                }
                break;
            }            
        }
        model.setNode( node );
        if(logger.isTraceEnabled()) logger.trace("< resolveBestNode {} {}",node.getPath());
        return node;
    }

    public Session retrieveSession(ModelImpl model) throws RepositoryException, LoginException
    {
        Session session = model.getSession();
        if ( session == null )
        {
            //if(model.getVirtualHostPath()!=null){
            //    session = retrieveSession( model.getVirtualHostPath(),model.getApp().getJcrWorkspace());
            //}else{
                session = retrieveSession();
            //}
            
            model.setSession( session );
        }
        return session;
    }


    public Session retrieveSession() throws RepositoryException, LoginException
    {
        if(logger.isTraceEnabled()) logger.trace("> retrieveSession");
        retrieveRepository();
        Session session = repository.login();
        if(session==null){
            // this may happens of repository was expired
            retrieveRepository();
            session = repository.login();
            Assert.notNull( session );
        }
        if(logger.isTraceEnabled()) logger.trace("< retrieveSession {}");
        return session;
    }


    public Repository retrieveRepository() throws RepositoryException
    {
        if ( repository == null)
        {
            Map<String, String> params = new HashMap<String, String>();
            repository = repositoryFactory.getRepository( params );
            Assert.notNull(repository);
            if ( logger.isDebugEnabled() )
                logger.debug( "loaded jcr repo from factory successfully" );
        }
        return repository;
    }


    public void setRepositoryFactory( RepositoryFactory repositoryFactory )
    {
        this.repositoryFactory = repositoryFactory;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void setPropertiesService( PropertiesService propertiesService )
    {
        this.propertiesService = propertiesService;
    }

}
