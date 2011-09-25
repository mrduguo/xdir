package org.duguo.xdir.core.internal.jcr;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.spi.model.Model;
import org.duguo.xdir.core.internal.app.JcrTemplateAwareApplication;
import org.duguo.xdir.core.internal.model.FormatService;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.core.internal.model.TextNode;
import org.duguo.xdir.core.internal.template.TemplateEngine;
import org.duguo.xdir.core.internal.utils.JcrNodeUtils;

public class JcrManagerApplication extends JcrTemplateAwareApplication
{
    private static final Logger logger = LoggerFactory.getLogger( JcrManagerApplication.class );

    private RepositoryFactory repositoryFactory;

    protected int handleInSession( ModelImpl model, int handleStatus ) throws Exception
    {
        getFormat().resolveFormat( model );
        setupAction( model );
        Node templateNode = model.getSession().getNode( model.getApp().getJcrBasePath() );
        model.setNode( templateNode );
        String repositoryName = model.getPathInfo().getCurrentPath();
        if ( repositoryName == null )
        {
            setupModelWithRepository(model);
        }else{
            model.getPathInfo().moveToNextPath();
            String workspaceName=model.getPathInfo().getCurrentPath();
            if ( workspaceName == null )
            {
                setupModelWithWorkspace(model,repositoryName);
            }else{
                return handleWithTargetJcrNode(model,repositoryName,workspaceName,handleStatus);
            }
        }
            if ( logger.isDebugEnabled() )
                logger.debug( "jcr browser handle site [{}]", model.getPathInfo().getRemainPath() );
            handleStatus = getTemplate().process( model, TemplateEngine.TEMPLATE_DEFAULT, model.getNodeType() );
        return handleStatus;
    }

    public boolean isRepositoryAvaliable( ModelImpl model )
    {
        try{
            return model.getNode().getSession().itemExists( "/" );
        }catch(Throwable ignore){}
        return false;
    }


    protected void setupModelWithRepository( ModelImpl model ) throws Exception
    {
        setupModelWithWorkspace( model, null);
    }

    @SuppressWarnings("unchecked")
    protected void setupModelWithWorkspace( ModelImpl model, String repositoryName ) throws Exception
    {
        setupPagePaths( model, repositoryName, null);
        if(repositoryName==null){
            model.setPageNavLevel3( listRepositories( model ) );
        }else{
            model.setPageNavLevel2( listRepositories( model ) );
            model.setPageNavLevel3( listWorkspaces( model,repositoryName ) );
        }
        Map repositoryAction=new HashMap();
        model.put( "repositoryAction", repositoryAction );
    }

    protected int handleWithTargetJcrNode( ModelImpl model, String repositoryName , String workspaceName, int handleStatus) throws Exception
    {
        setupPagePaths( model, repositoryName, workspaceName);
        boolean isNewSession=isDifferentSessionFromApplication(repositoryName,workspaceName);
        Session targetSession = null;
        if(isNewSession){
            targetSession = getJcrFactory().retriveSession( repositoryName,workspaceName );
            if ( logger.isDebugEnabled() )
                logger.debug( "session [{}/{}] retrived", repositoryName,workspaceName  );
        }else{
            targetSession=model.getSession();
        }
        try
        {   
            String jcrPath = null;
            model.getPathInfo().moveToNextPath();
            if(model.getPathInfo().getCurrentPath()!=null){
                jcrPath =  model.getPathInfo().getRemainPath();
            }else{
                jcrPath = "/";
            }
            if ( logger.isDebugEnabled() )
                logger.debug( "jcr path [{}]", jcrPath );
            Node jcrNode = null;
            if ( targetSession.itemExists( jcrPath ) )
            {
                jcrNode = targetSession.getNode( jcrPath );
            }else if ( targetSession.itemExists( jcrPath+model.getFormat() ) ){
                jcrNode = targetSession.getNode( jcrPath+model.getFormat() );  
                model.setFormat(FormatService.FORMAT_FOLDER);
            }
            if ( jcrNode!=null)
            {
                setupTargetJcrNodeNavLinks( model, jcrNode, repositoryName, workspaceName);
                if ( logger.isDebugEnabled() )
                    logger.debug( "jcr browser handle site [{}]", model.getPathInfo().getRemainPath() );
                model.setNodeType( Model.NULL_STRING );
                handleStatus = getTemplate().process( model, TemplateEngine.TEMPLATE_DEFAULT, model.getNodeType() );
            }
        }
        catch ( NoSuchWorkspaceException ex )
        {
            // workspace not found
        }
        catch ( IllegalArgumentException ex )
        {
            // repository not found
        }
        finally
        {
            if ( isNewSession )
            {
                targetSession.logout();
            }
        }
        return handleStatus;
    }


    private boolean isDifferentSessionFromApplication(String repositoryName, String workspaceName )
    {
        if(getJcrRepository().equals(repositoryName) && getJcrWorkspace().equals( workspaceName )){
            return false;
        }else{
            return true;
        }
    }


    protected void setupTargetJcrNodeNavLinks( ModelImpl model, Node jcrNode, String repositoryName, String workspaceName ) throws Exception
    {
        String jcrBasePath = model.getApp().getBaseUrl() + "/" + repositoryName+"/"+workspaceName;        
        if ( JcrNodeUtils.isRootNode( jcrNode ) )
        {
            model.setPageNavLevel1( listRepositories( model ) );
            model.setPageNavLevel2( listWorkspaces( model,repositoryName ) );
            model.setPageUrl(jcrBasePath+model.getFormat());
            model.setPageTitle(workspaceName);
        }
        else
        {
            if ( JcrNodeUtils.isRootNode( jcrNode.getParent() ) )
            {
                model.setPageNavLevel1( listWorkspaces( model,repositoryName ) );
                model.setPageNavLevel2( listChildren( model, jcrNode.getParent(), jcrBasePath ) );
            }
            else
            {
                model.setPageNavLevel1( listChildren( model, jcrNode.getParent().getParent(), jcrBasePath ) );
                model.setPageNavLevel2( listChildren( model, jcrNode.getParent(), jcrBasePath ) );
            }
            model.setPageUrl( jcrBasePath + jcrNode.getPath() + model.getFormat() );
            model.setPageTitle( jcrNode.getName() );
            addNodeParentsToPaths( model, jcrNode, jcrBasePath );
        }
        model.setPageNavLevel3( listChildren( model, jcrNode, jcrBasePath ) );
        model.setNode( jcrNode );
        if(model.getPageProperties().containsKey( "_nav_no_children" )){
            model.getPageProperties().remove( "_nav_no_children" );
        }
    }


    private void setupPagePaths( ModelImpl model, String repositoryName,String workspaceName ) throws Exception
    {
        List<Object> paths =new ArrayList<Object>();
        model.setPagePaths(paths);
        addPathsLink( model, null, model.displayPropertyValue( model.getApp().getSite().getUrl()),model.getApp().getSite().getTitle());
        if(repositoryName!=null){
            String jcrBasePath = model.getApp().getBaseUrl() + "/" + repositoryName;
            addPathsLink( model, null, jcrBasePath+model.getFormat(),repositoryName);
            if(workspaceName!=null){
                jcrBasePath = jcrBasePath+"/"+workspaceName;
                addPathsLink( model, null, jcrBasePath+model.getFormat(),workspaceName);
                model.setPageTitle(workspaceName);
            }else{
                model.setPageTitle(repositoryName);
            }
            model.setPageUrl(jcrBasePath+model.getFormat());
        }
    }


    protected Map<String, Object> listChildren( ModelImpl model, Node currentNode, String jcrBasePath )
        throws Exception
    {
        NodeIterator nodeIterator = currentNode.getNodes();
        Map<String, Object> childrenMap = new HashMap<String, Object>();
        int index = 0;
        while ( nodeIterator.hasNext() )
        {
            Node childNode = ( Node ) nodeIterator.next();
            String nodeName = childNode.getName();
            String nodeLink = jcrBasePath + childNode.getPath() + model.getFormat();
            TextNode textLink = new TextNode( childNode, nodeLink, nodeName );
            childrenMap.put( nodeName + index, textLink );
            index++;
        }
        return childrenMap;
    }

    @SuppressWarnings("unchecked")
    public void performRepositoryAction( ModelImpl model,String repositoryName,String action ) throws Exception
    {
        Map repositoryAction=(Map)model.get("repositoryAction");
        repositoryAction.put( "repositoryName", repositoryName);
        repositoryAction.put( "action", action);
        String status=null;
        try{
            repositoryFactory.getRepository( repositoryAction );
            status=(String)repositoryAction.get( "status" );
        }catch(Exception ex){
            logger.error( "failed to perform repository action",ex );
            status=ex.getMessage();
        }
        if(status!=null){
            model.setStatus( status );
        }
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> listRepositories( ModelImpl model ) throws Exception
    {
        Map repositoryList=new HashMap();
        repositoryList.put( "action", "repositoryList" );
        repositoryFactory.getRepository( repositoryList );
        model.put( "repositoryList",repositoryList);

        Map<String, Object> childrenMap = new HashMap<String, Object>();
        Map repositories=(Map)repositoryList.get( "repositories" );
        for ( Object repositoryObj : repositories.keySet())
        {
            String repositoryName=(String)repositoryObj;
            String nodeLink = model.getApp().getBaseUrl() + "/" + repositoryName + model.getFormat();
            TextNode textLink = new TextNode( null, nodeLink, repositoryName );
            childrenMap.put( repositoryName, textLink );
        }
        return childrenMap;
    }


    @SuppressWarnings("unchecked")
    protected Map<String, Object> listWorkspaces( ModelImpl model, String repositoryName ) throws Exception
    {
        Map workspaceList=new HashMap();
        workspaceList.put( "action", "workspaceList" );
        workspaceList.put( "repositoryName", repositoryName );
        repositoryFactory.getRepository( workspaceList );
        model.put( "workspaceList",workspaceList);

        Map<String, Object> childrenMap = new HashMap<String, Object>();
        Map workspaces=(Map)workspaceList.get( "workspaces" );
        for ( Object workspaceObj : workspaces.keySet())
        {
            String workspaceName=(String)workspaceObj;
            String nodeLink = model.getApp().getBaseUrl() + "/" + repositoryName + "/" + workspaceName + model.getFormat();
            TextNode textLink = new TextNode( null, nodeLink, workspaceName );
            childrenMap.put( workspaceName, textLink );
        }
        return childrenMap;
    }


    protected void addNodeParentsToPaths( ModelImpl model, Node jcrNode, String jcrBasePath ) throws Exception
    {
        if ( !JcrNodeUtils.isRootNode( jcrNode ) )
        {
            addNodeParentsToPaths( model, jcrNode.getParent(), jcrBasePath );
            addPathsLink( model, jcrNode, jcrBasePath+jcrNode.getPath()+model.getFormat(),jcrNode.getName() );
        }
    }


    private void addPathsLink( ModelImpl model, Node jcrNode, String linkUrl,String title ) throws Exception
    {
        TextNode textLink = new TextNode( jcrNode, linkUrl, title);
        model.getPagePaths().add( textLink );
    }


    public void setRepositoryFactory( RepositoryFactory repositoryFactory )
    {
        this.repositoryFactory = repositoryFactory;
    }

}
