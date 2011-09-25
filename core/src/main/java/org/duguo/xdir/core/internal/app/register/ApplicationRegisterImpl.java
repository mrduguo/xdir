package org.duguo.xdir.core.internal.app.register;


import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.duguo.xdir.core.internal.app.Application;
import org.duguo.xdir.core.internal.app.JcrTemplateAwareApplication;
import org.duguo.xdir.core.internal.site.Site;
import org.duguo.xdir.core.internal.utils.JcrNodeUtils;
import org.duguo.xdir.core.internal.utils.JcrRepositoryUtils;
import org.duguo.xdir.core.internal.utils.JcrRepositoryUtils.RepoPath;
import org.duguo.xdir.util.http.HttpUtil;


public class ApplicationRegisterImpl extends AbstractApplicationRegister
{
    
    private static final Logger logger = LoggerFactory.getLogger( ApplicationRegisterImpl.class );


    public void afterPropertiesSet() throws Exception
    {
        loadRootSite();
    }

    public void register( String path, Application application ) throws Exception
    {
        String[] paths=path.split( "/" );
        Assert.state( paths.length<2, "you are not allowed to register root application" );

                String rootApplicationName=((JcrTemplateAwareApplication)application).getSite().getName();
                if(paths[0].equals( rootApplicationName )){
                    String[] subPaths=new String[paths.length-1];
                    System.arraycopy( paths, 0, subPaths, 0, paths.length-1 );
                    registerApplication( application, subPaths );
                    if(logger.isDebugEnabled())
                        logger.debug("register application [{}] success",path);
                    return;
                }
        if(logger.isDebugEnabled())
            logger.debug("register application [{}] can not found root",path);
    }


    public void unregister( String path ) throws Exception
    {
        String[] paths=path.split( "/" );
        Assert.state( paths.length<2, "you are not allowed to unregister root application, which could be unregisterd by unregister the servlet" );
        
            Application application=rootApplication;
                String rootApplicationName=((JcrTemplateAwareApplication)application).getSite().getName();
                if(paths[0].equals( rootApplicationName )){
                    for(int i=1;i<paths.length;i++){
                        application=application.getChildren().get( paths[i] );
                        if(application==null){
                            break;
                        }
                        if(i==paths.length-1){
                            application.getParent().getChildren().remove( paths[i] );
                            if(logger.isDebugEnabled())
                                logger.debug("unregister application [{}] success",path);
                            return;
                        }
                    }
            }
        if(logger.isDebugEnabled())
            logger.debug("unregister application [{}] not found",path);
    }


    public void loadRootSite() throws Exception
    {
        RepoPath repoPath = JcrRepositoryUtils.parseRepoPath( sitesRoot );
        Session session = jcrFactory.retriveSession( repoPath.getRepositoryName(), repoPath.getWorkspaceName() );
        try
        {
            if ( session.itemExists( repoPath.getAbsPath() ) )
            {
                if(logger.isDebugEnabled())
                    logger.debug( "loading applications from [{}]", sitesRoot );
                
                Node configNode = session.getNode( repoPath.getAbsPath() );
                jcrFactory.bindValueToObject( configNode, rootApplication, "_application_" );
                Site rootSite =registerSite( configNode, null );
                scanChildSites( configNode, rootSite );
                
                if(logger.isDebugEnabled())
                    logger.debug( "loaded applications from [{}]", sitesRoot );
            }
            else
            {
                logger.warn( "applications root not found [{}]", sitesRoot );
            }
        }
        finally
        {
            session.logout();
        }
    }

    protected void scanChildSites(Node configNode,Site parentSite) throws Exception
    {   
        NodeIterator children = configNode.getNodes();
        while ( children.hasNext() )
        {
            Node childNode = children.nextNode();
            Site childSite =registerSite( childNode, parentSite );
            scanChildSites( childNode,childSite);
        }
    }


    protected Site registerSite( Node configNode, Site parentSite) throws Exception
    {
        Site site = new Site();
        jcrFactory.bindValueToObject( configNode, site, "_site_" );
        
        System.out.println("\n\nbinded site url:"+site.getUrl());
        site.setName( configNode.getName());
        if(configNode.hasProperty( "_site_metasite" )){
            site.setParent( parentSite );
        }else{
            addSiteToParent(parentSite,site);
        }
        
        if(site.getTitle()==null){
            site.setTitle( JcrNodeUtils.getNodeTitle( configNode ) );
        }
        if(site.getUrl()==null){
            if(parentSite==null){
                site.setUrl( buildDefaultApplicationUrl( rootApplication.getBaseUrl() ));
            }else{
                site.setUrl(HttpUtil.autoDetectRelativeUrl( site.getParent().getUrl(), site.getName() ));
            }
        }

        NearestApplication nearestApp=retriveNearestApplication(site);
        if(site.getGlobalPageTitle()==null){
            site.setGlobalSite(nearestApp.getApplication().getSite().getGlobalSite());
        }
        if(site.getGlobalUrl()==null){
            // for root site
            String globalUrl=site.getUrl();
            site.setGlobalUrl(globalUrl);            
        }
        if(parentSite!=null){
            lookupApplication( configNode, site );
        }
        return site;
    }

    protected void addSiteToParent( Site parentSite, Site site )
    {
        if(parentSite==null){
            rootApplication.setSite( site );
            site.setApp( rootApplication );
        }else{
            
            Map<String, Site> siteChildren = parentSite.getChildren();
            if(siteChildren==null){
                siteChildren=new HashMap<String, Site>();
                parentSite.setChildren( siteChildren );
            }
            siteChildren.put( site.getName(), site );            
            site.setParent( parentSite );
        }
    }


}
