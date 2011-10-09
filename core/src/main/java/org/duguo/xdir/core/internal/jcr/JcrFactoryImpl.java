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
import org.duguo.xdir.core.internal.utils.JcrNodeUtils;
import org.duguo.xdir.util.bean.BeanUtil;


public class JcrFactoryImpl implements JcrFactory
{
    private static final Logger logger = LoggerFactory.getLogger( JcrFactoryImpl.class );

    private RepositoryFactory repositoryFactory;
    private Map<String, Repository> repositories = new HashMap<String, Repository>();
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
        if ( logger.isDebugEnabled() )
            logger.debug( "resolved best node with remain path [" + model.getPathInfo().getRemainPath() + "]" );
        model.setNode( node );
        return node;
    }

    public Session retriveSession( ModelImpl model ) throws RepositoryException, LoginException
    {
        Session session = model.getSession();
        if ( session == null )
        {
            //if(model.getVirtualHostPath()!=null){
            //    session = retriveSession( model.getVirtualHostPath(),model.getApp().getJcrWorkspace());
            //}else{
                session = retriveSession( model.getApp().getJcrRepository(),model.getApp().getJcrWorkspace());
            //}
            
            model.setSession( session );
        }
        return session;
    }


    public Session retriveSession( String repositoryName,String workspaceName ) throws RepositoryException, LoginException
    {
        Repository repository = retriveRepository( repositoryName );
        Session session = repository.login(workspaceName);
        if(session==null){
            // this may happens of repository was expired
            repositories.remove( repositoryName );
            repository = retriveRepository( repositoryName );
            session = repository.login(workspaceName);
            Assert.notNull( session );
        }
        if ( logger.isDebugEnabled() )
            logger.debug( "session retrived" );
        return session;
    }


    public Repository retriveRepository( String repositoryName ) throws RepositoryException
    {
        Repository repository = repositories.get( repositoryName );
        if ( repository == null)
        {
            Map<String, String> params = new HashMap<String, String>();
            params.put( "repositoryName", repositoryName );
            repository = repositoryFactory.getRepository( params );
            Assert.notNull( repository );
            repositories.put( repositoryName, repository );
            if ( logger.isDebugEnabled() )
                logger.debug( "get repository [" + repositoryName + "] from factory successfully" );
        }
        return repository;
    }


    public void setRepositoryFactory( RepositoryFactory repositoryFactory )
    {
        this.repositoryFactory = repositoryFactory;
    }


    public void setRepositories( Map<String, Repository> repositories )
    {
        this.repositories = repositories;
    }

    public void setPropertiesService( PropertiesService propertiesService )
    {
        this.propertiesService = propertiesService;
    }

}
