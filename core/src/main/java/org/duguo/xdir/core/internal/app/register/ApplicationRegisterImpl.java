package org.duguo.xdir.core.internal.app.register;


import java.util.*;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.duguo.xdir.core.internal.app.SimplePathApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.duguo.xdir.core.internal.app.Application;
import org.duguo.xdir.core.internal.app.JcrTemplateAwareApplication;
import org.duguo.xdir.core.internal.site.Site;
import org.duguo.xdir.core.internal.utils.JcrRepositoryUtils;
import org.duguo.xdir.core.internal.utils.JcrRepositoryUtils.RepoPath;


public class ApplicationRegisterImpl extends AbstractApplicationRegister {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationRegisterImpl.class);


    public void afterPropertiesSet() throws Exception {
        scanApps();
    }

    public void register(String path, Application application) throws Exception {
//        String[] paths = path.split("/");
//        Assert.state(paths.length < 2, "you are not allowed to register root application");
//
//        String rootApplicationName = ((JcrTemplateAwareApplication) application).getSite().getName();
//        if (paths[0].equals(rootApplicationName)) {
//            String[] subPaths = new String[paths.length - 1];
//            System.arraycopy(paths, 0, subPaths, 0, paths.length - 1);
//            registerApplication(application, subPaths);
//            if (logger.isDebugEnabled())
//                logger.debug("register application [{}] success", path);
//            return;
//        }
//        if (logger.isDebugEnabled())
//            logger.debug("register application [{}] can not found root", path);
    }


    public void unregister(String path) throws Exception {
        String[] paths = path.split("/");
        Assert.state(paths.length < 2, "you are not allowed to unregister root application, which could be unregisterd by unregister the servlet");

        Application application = rootApplication;
        String rootApplicationName = ((JcrTemplateAwareApplication) application).getSite().getName();
        if (paths[0].equals(rootApplicationName)) {
            for (int i = 1; i < paths.length; i++) {
                application = application.getChildren().get(paths[i]);
                if (application == null) {
                    break;
                }
                if (i == paths.length - 1) {
                    application.getParent().getChildren().remove(paths[i]);
                    if (logger.isDebugEnabled())
                        logger.debug("unregister application [{}] success", path);
                    return;
                }
            }
        }
        if (logger.isDebugEnabled())
            logger.debug("unregister application [{}] not found", path);
    }


    public void scanApps() throws Exception {
        if(logger.isTraceEnabled())
            logger.trace("> scanApps");

        RepoPath repoPath = JcrRepositoryUtils.parseRepoPath(appsRoot);
        Session session = jcrFactory.retriveSession(repoPath.getRepositoryName(), repoPath.getWorkspaceName());
        try {
            if (session.itemExists(repoPath.getAbsPath())) {
                if (logger.isDebugEnabled())
                    logger.debug("loading applications from [{}]", appsRoot);
                Node appNode = session.getNode(repoPath.getAbsPath());
                scanAppNode(appNode, null);

                StringBuilder appsString = new StringBuilder();
                printAppsTree("root",getRootApplication(), appsString, 0);
                logger.info("registered apps\n{}",  appsString);
                if (logger.isDebugEnabled())
                    logger.debug("loaded applications from [{}]", appsRoot);
            } else {
                logger.warn("applications root not found [{}]", appsRoot);
            }
        } finally {
            session.logout();
        }

        if(logger.isTraceEnabled())
            logger.trace("< scanApps");
    }

    private void printAppsTree(String appName,Application application, StringBuilder appsString, int level) {
        appsString.append("\n");
        if (level > 0) {
            for (int i = 0; i < level; i++) {
                appsString.append("  ");
            }
        }
        appsString.append(appName);
        for(Map.Entry<String,Application> entry:application.getChildren().entrySet()){
            printAppsTree(entry.getKey(),entry.getValue(), appsString, level+1);
        }
    }

    private void scanAppNode(Node appNode, Application parentApp) throws Exception {
        Application currentApp = registerApp(appNode, parentApp);
        NodeIterator children = appNode.getNodes();
        while (children.hasNext()) {
            Node childNode = children.nextNode();
            scanAppNode(childNode, currentApp);
        }
    }


    protected Application registerApp(Node appNode, Application parentApp) throws Exception {
        if(logger.isTraceEnabled())
            logger.trace("> registerApp({})",appNode.getPath());
        Application application = null;
        if (appNode.hasProperty("_application_jcr_base_path")) {
            String applicationBeanName;
            if (appNode.hasProperty("_application_bean")) {
                applicationBeanName = appNode.getProperty("_application_bean").getString();
            } else {
                applicationBeanName = defaultApplicationPrototypeBeanName;
            }
            application = beanFactory.getBean(applicationBeanName, Application.class);
            jcrFactory.bindValueToObject(appNode, application, "_application_");

            if (application instanceof JcrTemplateAwareApplication) {
                JcrTemplateAwareApplication nearestJcrTemplateAwareApplication = retrieveNearestJcrTemplateAwareApplication(parentApp);
                JcrTemplateAwareApplication jcrTemplateAwareApplication = (JcrTemplateAwareApplication) application;
                copyRequiredField(nearestJcrTemplateAwareApplication, jcrTemplateAwareApplication,
                        "formats", "format", "resource", "template", "jcrRepository", "jcrWorkspace", "templatePaths");
            }
            invokeInitIfExist(application);
            if (logger.isDebugEnabled())
                logger.debug("created application [" + applicationBeanName + ":{}] from [{}]", application.getClass().getSimpleName(), appNode.getPath());
        } else {
            application = new SimplePathApplication();
        }
        if(parentApp!=null){
            parentApp.getChildren().put(appNode.getName(),application);
            if(application instanceof SimplePathApplication)
                ((SimplePathApplication)application).setParent(parentApp);
        }

        if(logger.isTraceEnabled())
            logger.trace("< registerApp with {}",application);
        return application;
    }

}
