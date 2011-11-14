package org.duguo.xdir.core.internal.app.register;


import java.lang.reflect.Method;
import java.util.*;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
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
import org.duguo.xdir.core.internal.utils.JcrRepositoryUtils;
import org.duguo.xdir.core.internal.utils.JcrRepositoryUtils.RepoPath;
import org.duguo.xdir.http.service.ServletService;
import org.duguo.xdir.util.bean.BeanUtil;
import org.duguo.xdir.util.bean.BeanUtil.GetterSetterCallback;


public abstract class AbstractApplicationRegister implements BeanFactoryAware, InitializingBean, ApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractApplicationRegister.class);

    protected BeanFactory beanFactory;
    protected ServletService servletService;
    protected JcrFactory jcrFactory;

    protected JcrTemplateAwareApplication rootApplication;
    protected String defaultApplicationPrototypeBeanName = "defaultApplicationPrototype";

    protected String appsRoot;

    protected String buildDefaultApplicationUrl(String baseUrl) {
        return baseUrl + "/" + TemplateEngine.TEMPLATE_DEFAULT + "$[format]";
    }


    protected void registerApplication(Node siteNode, Site site, Application application) throws Exception {
        if (!application.equals(rootApplication)) {
            Application parentApp = null;
            List<String> paths;
            if (siteNode.hasProperty("_application_controller_path")) {
                parentApp = rootApplication;
                String[] splitedPaths = siteNode.getProperty("_application_controller_path").getString().split("/");
                paths = Arrays.asList(splitedPaths);
            } else {
                paths = new ArrayList<String>();
                paths.add(site.getName());
                while (site.getParent() != null) {
                    site = site.getParent();
                    if (site.getApp() != null) {
                        parentApp = site.getApp();
                        break;
                    }
                    paths.add(0, site.getName());
                }
                if (parentApp == null) {
                    throw new IllegalArgumentException("_application_controller_path is required for site root at " + siteNode.getPath());
                }
            }
            registerChildAppToParentApp(parentApp, application, paths);
        }
    }

    protected void registerChildAppToParentApp(Application parentApp, Application childApp, List<String> paths) {
        for (int i = 0; i < paths.size(); i++) {
            String pathName = paths.get(i);
            if (i == paths.size() - 1) {
                if (parentApp.getChildren().containsKey(pathName)) {
                    logger.warn("replace existing app at [{}]", pathName);
                    childApp.getChildren().putAll(parentApp.getChildren().get(pathName).getChildren());
                }
                parentApp.getChildren().put(pathName, childApp);
                if (logger.isDebugEnabled())
                    logger.debug("registered parent:child app <{}> with paths <{}>", parentApp.toString() + ":" + childApp.toString(), StringUtils.join(paths, ","));
                logger.error("registered parent:child app <{}> with paths <{}>", parentApp.toString() + ":" + childApp.toString(), StringUtils.join(paths, ","));
            } else {
                parentApp = getOrCreateDefaultApplication(parentApp, pathName);
            }
        }
    }


    protected void invokeInitIfExist(Object object) throws Exception {
        try {
            Method initMethod = object.getClass().getDeclaredMethod("init", new Class[0]);
            initMethod.invoke(object, new Object[0]);
            if (logger.isDebugEnabled())
                logger.debug("init invoked");
        } catch (NoSuchMethodException ignore) {
        }
    }


    protected Application getOrCreateDefaultApplication(Application parentApplication, String name) {
        Application childApplication = parentApplication.getChildren().get(name);
        if (childApplication == null) {
            ParentAwareApplication newApplication = new ParentAwareApplication();
            parentApplication.getChildren().put(name, newApplication);
            newApplication.setParent(parentApplication);
            childApplication = newApplication;
            if (logger.isDebugEnabled())
                logger.debug("registered new child default application [" + name + "]");
        }
        return childApplication;
    }


    protected boolean isValidJcrApplication(Node configNode, JcrTemplateAwareApplication application) throws Exception {
        RepoPath repoPath = JcrRepositoryUtils.parseRepoPath(appsRoot);
        boolean isNewSession = false;
        boolean isNodeExist = false;
        Session targetSession = null;
        if (repoPath.getRepositoryName().equals(application.getJcrRepository()) && repoPath.getWorkspaceName().equals(application.getJcrWorkspace())) {
            isNewSession = false;
            targetSession = configNode.getSession();
        } else {
            isNewSession = true;
            targetSession = jcrFactory.retriveSession(application.getJcrRepository(), application.getJcrWorkspace());
        }

        try {
            isNodeExist = targetSession.itemExists(application.getJcrBasePath());
            if (logger.isDebugEnabled())
                logger.debug("jcr path [{}]{} exist", application.getJcrRepository() + "/" + repoPath.getWorkspaceName() + application.getJcrBasePath(), (isNodeExist ? "" : " doesn't"));
        } finally {
            if (isNewSession) {
                targetSession.logout();
            }
        }
        return isNodeExist;
    }


    protected JcrTemplateAwareApplication retrieveNearestJcrTemplateAwareApplication(Application parentApp) {
        do{
            if(parentApp==null){
                break;
            }else if(parentApp instanceof JcrTemplateAwareApplication){
                return (JcrTemplateAwareApplication)parentApp;
            }else{
                parentApp=parentApp.getParent();
            }
        }while(true);
        return rootApplication;
    }


    /**
     * generate site paths without root site
     */
    protected String[] buildSitePaths(Site site) {
        List<String> paths = new ArrayList<String>();
        Site parentSite = site;
        while (parentSite.getParent() != null) {
            paths.add(0, parentSite.getName());
            parentSite = parentSite.getParent();
        }
        return paths.toArray(new String[0]);
    }

    protected void copyRequiredField(Object sourceObject, Object targetObject, String... fieldNames) {
        Assert.notNull(sourceObject);
        for (String fieldName : fieldNames) {
            GetterSetterCallback<Object> sourceFieldGetterAndSetter = BeanUtil.getBeanGetterSetterIfAvailable(sourceObject, Object.class, fieldName);
            GetterSetterCallback<Object> targetFieldGetterAndSetter = BeanUtil.getBeanGetterSetterIfAvailable(targetObject, Object.class, fieldName);
            Assert.notNull(sourceFieldGetterAndSetter, fieldName);
            Assert.notNull(targetFieldGetterAndSetter, fieldName);
            if (targetFieldGetterAndSetter.get() == null) {
                Object fieldValue = sourceFieldGetterAndSetter.get();
                Assert.notNull(fieldValue, fieldName);
                targetFieldGetterAndSetter.set(fieldValue);
                if (logger.isDebugEnabled())
                    logger.debug("copied required field [{}]", fieldName);
            }
        }
    }


    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    public void setServletService(ServletService servletService) {
        this.servletService = servletService;
    }


    public void setJcrFactory(JcrFactory jcrFactory) {
        this.jcrFactory = jcrFactory;
    }

    public void setDefaultApplicationPrototypeBeanName(String defaultApplicationPrototypeBeanName) {
        this.defaultApplicationPrototypeBeanName = defaultApplicationPrototypeBeanName;
    }


    public JcrTemplateAwareApplication getRootApplication() {
        return rootApplication;
    }


    public void setRootApplication(JcrTemplateAwareApplication rootApplication) {
        this.rootApplication = rootApplication;
    }

    public String getAppsRoot() {
        return appsRoot;
    }

    public void setAppsRoot(String appsRoot) {
        this.appsRoot = appsRoot;
    }
}
