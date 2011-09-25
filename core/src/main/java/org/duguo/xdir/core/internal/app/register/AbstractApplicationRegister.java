package org.duguo.xdir.core.internal.app.register;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.duguo.xdir.core.internal.app.Application;
import org.duguo.xdir.core.internal.app.JcrTemplateAwareApplication;
import org.duguo.xdir.core.internal.app.ParentAwareApplication;
import org.duguo.xdir.core.internal.jcr.JcrFactory;
import org.duguo.xdir.core.internal.site.Site;
import org.duguo.xdir.core.internal.template.TemplateEngine;
import org.duguo.xdir.core.internal.utils.JcrNodeUtils;
import org.duguo.xdir.core.internal.utils.JcrRepositoryUtils;
import org.duguo.xdir.core.internal.utils.JcrRepositoryUtils.RepoPath;
import org.duguo.xdir.http.service.ServletService;
import org.duguo.xdir.util.bean.BeanUtil;
import org.duguo.xdir.util.bean.BeanUtil.GetterSetterCallback;


public abstract class AbstractApplicationRegister implements BeanFactoryAware, InitializingBean, ApplicationService
{
    
    private static final Logger logger = LoggerFactory.getLogger( AbstractApplicationRegister.class );

    protected BeanFactory beanFactory;
    protected ServletService servletService;
    protected JcrFactory jcrFactory;
    
    protected JcrTemplateAwareApplication rootApplication;
    protected String defaultApplicationPrototypeBeanName="defaultApplicationPrototype";
    
    protected String sitesRoot;

    protected String buildDefaultApplicationUrl( String baseUrl)
    {
        return baseUrl+"/"+TemplateEngine.TEMPLATE_DEFAULT+"$[format]";
    }


    protected void lookupApplication(  Node configNode, Site site ) throws Exception
    {
        if ( configNode.hasProperty( "_application_jcr_base_path" ))
        {
            String applicationBeanName = JcrNodeUtils.retrivePropertyIncludeParent( configNode, "_application_bean");
            if(applicationBeanName==null){
                applicationBeanName=defaultApplicationPrototypeBeanName;
            }
            Application application = beanFactory.getBean( applicationBeanName, Application.class );
            jcrFactory.bindValueToObject( configNode, application, "_application_" );

            if ( application instanceof JcrTemplateAwareApplication )
            {
                NearestApplication nearestApp=retriveNearestApplication(site);
                JcrTemplateAwareApplication jcrTemplateAwareApplication = ( JcrTemplateAwareApplication ) application;
                copyRequiredField( nearestApp.getApplication(), jcrTemplateAwareApplication,
                    "formats","format","resource", "template","jcrRepository","jcrWorkspace" );
                if(!isValidJcrApplication(configNode,jcrTemplateAwareApplication)){
                    return;
                }
                
                if(jcrTemplateAwareApplication.getTemplatePaths()==null){
                    String[] templatePaths=nearestApp.getApplication().getTemplatePaths();
                    jcrTemplateAwareApplication.setTemplatePaths( templatePaths );
                }
                if(jcrTemplateAwareApplication.getBaseUrl()==null){
                    String baseUrl=nearestApp.getApplication().getBaseUrl();
                    baseUrl=baseUrl+nearestApp.getRelativePath();
                    if ( logger.isDebugEnabled() )
                        logger.debug( "set application base url [{}]",baseUrl);
                    jcrTemplateAwareApplication.setBaseUrl( baseUrl );
                }
                jcrTemplateAwareApplication.setSite( site );
                if(!configNode.hasProperty( "_site_url" )){
                    site.setUrl( buildDefaultApplicationUrl( jcrTemplateAwareApplication.getBaseUrl() ) );
                }
            }
            site.setApp( application );

            String[] paths = buildSitePaths( site );
            registerApplication(application,paths);
            invokeInitIfExist(application);
            if ( logger.isDebugEnabled() )
                logger.debug( "created application ["+applicationBeanName+":{}] from [{}]", application.getClass().getSimpleName(), configNode.getPath() );
        }

        
        
    }

    protected void registerApplication( Application application, String[] paths )
    {
        Application parentApplication=rootApplication;
        for(int i=0;i<paths.length;i++){
            String pathName=paths[ i];
            if ( i == paths.length - 1 )
            {
                if(parentApplication.getChildren().containsKey( pathName )){
                    logger.warn( "override exist application at [{}]", pathName);
                }
                Application existApplication=parentApplication.getChildren().put( pathName, application );
                if(existApplication!=null && existApplication.getChildren().size()>0){
                    for(Map.Entry<String,Application> entry:existApplication.getChildren().entrySet()){
                        if(application.getChildren().containsKey( entry.getKey())){
                            logger.warn( "ignore exist child application [{}]", entry.getKey());
                        }else{
                            application.getChildren().put( entry.getKey() , entry.getValue() );
                        }
                    }
                }
                if(logger.isDebugEnabled())
                    logger.debug( "registered application [{}] with name [{}]",application.getClass().getSimpleName(),pathName);
            }
            else
            {
                parentApplication = getOrCreateDefaultApplication( parentApplication, pathName);
            }
        }
    }


    protected void invokeInitIfExist( Object object )throws Exception
    {
        try{
            Method initMethod=object.getClass().getDeclaredMethod( "init", new Class[0] );
            initMethod.invoke( object, new Object[0]);
            if(logger.isDebugEnabled())
                logger.debug("init invoked");
        }catch(NoSuchMethodException ignore){}
    }


    protected Application getOrCreateDefaultApplication( Application parentApplication, String name )
    {
        Application childApplication = parentApplication.getChildren().get( name );
        if ( childApplication == null )
        {
            ParentAwareApplication newApplication = new ParentAwareApplication();
            parentApplication.getChildren().put( name, newApplication );
            newApplication.setParent( parentApplication );
            childApplication=newApplication;
            if ( logger.isDebugEnabled() )
                logger.debug( "registered new child default application [" + name + "]" );
        }
        return childApplication;
    }


    protected boolean isValidJcrApplication(Node configNode, JcrTemplateAwareApplication application ) throws Exception
    {
        RepoPath repoPath = JcrRepositoryUtils.parseRepoPath( sitesRoot );
        boolean isNewSession=false;
        boolean isNodeExist=false;
        Session targetSession=null;
        if(repoPath.getRepositoryName().equals( application.getJcrRepository() ) && repoPath.getWorkspaceName().equals( application.getJcrWorkspace() )){
            isNewSession=false;
            targetSession=configNode.getSession();
        }else{
            isNewSession=true;
            targetSession=jcrFactory.retriveSession( application.getJcrRepository(), application.getJcrWorkspace() );
        }
        
        try{
            isNodeExist=targetSession.itemExists( application.getJcrBasePath() );
            if(logger.isDebugEnabled())
                logger.debug( "jcr path [{}]{} exist",application.getJcrRepository() +"/"+ repoPath.getWorkspaceName()+application.getJcrBasePath(),(isNodeExist?"":" doesn't") );
        }finally{
            if(isNewSession){
                targetSession.logout();
            }
        }
        return isNodeExist;
    }

    

    protected NearestApplication retriveNearestApplication(Site currentSite)
    {
        NearestApplication nearestApp=new NearestApplication();
        if(currentSite.getParent()==null){
            // skip root site
            nearestApp.setApplication( rootApplication );
            return nearestApp;
        }
        
        String[] paths = buildSitePaths( currentSite );
        
        // step 2 lookup the JcrTemplateAwareApplication and put in rootApplication
        int foundedPathIndex=0;
        Application currentApplication=rootApplication;
        JcrTemplateAwareApplication foundedApp=rootApplication;
        if(paths.length>1){
            for(int i=0;i<paths.length-1;i++){
                currentApplication=currentApplication.getChildren().get( paths[ i ]);
                if(currentApplication==null){
                    break;
                }
                if(currentApplication instanceof JcrTemplateAwareApplication){
                    foundedPathIndex=i+1;
                    foundedApp=(JcrTemplateAwareApplication)currentApplication;                
                }
            }
        }
        // setp 3 build relative path
        StringBuilder relativePath=new StringBuilder();
        for(int i=foundedPathIndex;i<paths.length;i++){
            relativePath.append( "/" );
            relativePath.append(paths[i]);
        }
        nearestApp.setRelativePath(relativePath.toString());
        nearestApp.setApplication(foundedApp);
        if(logger.isDebugEnabled())
            logger.debug( "retrived nearest application [{}] with relative path [{}]",foundedApp.getJcrBasePath(),nearestApp.getRelativePath() );
        
        return nearestApp;
    }


    /**
     *  generate site paths without root site
     */
    protected String[] buildSitePaths( Site site )
    {
        List<String> paths=new ArrayList<String>();
        Site parentSite=site;
        while(parentSite.getParent()!=null){
            paths.add(0, parentSite.getName() );
            parentSite=parentSite.getParent();
        }
        return paths.toArray( new String[paths.size()] );
    }

    protected void copyRequiredField( Object sourceObject, Object targetObject, String... fieldNames )
    {
        Assert.notNull( sourceObject );
        for ( String fieldName : fieldNames )
        {
            GetterSetterCallback<Object> sourceFieldGetterAndSetter = BeanUtil.getBeanGetterSetterIfAvailable(sourceObject, Object.class, fieldName );
            GetterSetterCallback<Object> targetFieldGetterAndSetter = BeanUtil.getBeanGetterSetterIfAvailable(targetObject, Object.class, fieldName );
            Assert.notNull( sourceFieldGetterAndSetter, fieldName );
            Assert.notNull( targetFieldGetterAndSetter, fieldName );
            if ( targetFieldGetterAndSetter.get() == null )
            {
                Object fieldValue = sourceFieldGetterAndSetter.get();
                Assert.notNull( fieldValue, fieldName );
                targetFieldGetterAndSetter.set( fieldValue );
                if ( logger.isDebugEnabled() )
                    logger.debug( "copied required field [{}]", fieldName );
            }
        }
    }


    public void setBeanFactory( BeanFactory beanFactory )
    {
        this.beanFactory = beanFactory;
    }


    public void setServletService( ServletService servletService )
    {
        this.servletService = servletService;
    }


    public void setJcrFactory( JcrFactory jcrFactory )
    {
        this.jcrFactory = jcrFactory;
    }

    public void setDefaultApplicationPrototypeBeanName( String defaultApplicationPrototypeBeanName )
    {
        this.defaultApplicationPrototypeBeanName = defaultApplicationPrototypeBeanName;
    }


    public JcrTemplateAwareApplication getRootApplication()
    {
        return rootApplication;
    }


    public void setRootApplication( JcrTemplateAwareApplication rootApplication )
    {
        this.rootApplication = rootApplication;
    }


    public String getSitesRoot()
    {
        return sitesRoot;
    }


    public void setSitesRoot( String sitesRoot )
    {
        this.sitesRoot = sitesRoot;
    }

}
