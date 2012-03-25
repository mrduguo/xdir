package org.duguo.xdir.core.internal.app.register;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.duguo.xdir.core.internal.app.Application;
import org.duguo.xdir.core.internal.app.JcrTemplateAwareApplication;
import org.duguo.xdir.core.internal.app.SimplePathApplication;
import org.duguo.xdir.core.internal.app.resource.ResourceApplication;
import org.duguo.xdir.core.internal.config.PropertiesService;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.lang.management.ManagementFactory;
import java.util.Map;


public class ApplicationRegisterImpl extends AbstractApplicationRegister implements BundleContextAware{

    private static final Logger logger = LoggerFactory.getLogger(ApplicationRegisterImpl.class);
    private PropertiesService propertiesService;
    private BundleContext bundleContext;

    public void init()throws Exception{
        reload();
        long jvmUpTime = ManagementFactory.getRuntimeMXBean().getUptime();

        setupXdirServerInfo(jvmUpTime);

    }
    private void setupXdirServerInfo(long jvmUpTime) {
        StringBuilder startupMessage =new StringBuilder();
        startupMessage.append("XDir server started in ");
        startupMessage.append(DurationFormatUtils.formatDurationWords(jvmUpTime, true, true));

        String serverVersion = bundleContext.getBundle().getVersion().toString();
        if(System.getProperty("xdir.server.version")==null) System.setProperty("xdir.server.version",serverVersion);
        appendInfoLine(startupMessage, "XDir Core Version", serverVersion);

        String serverBuildTime = DateFormatUtils.format(bundleContext.getBundle().getLastModified(), "yyyy-MM-dd HH:mm:ss");
        if(System.getProperty("xdir.server.build.time")==null) System.setProperty("xdir.server.build.time",serverBuildTime);
        appendInfoLine(startupMessage, "XDir Core Build Time", serverBuildTime);

        appendInfoLine(startupMessage, "XDir Home", System.getProperty("xdir.home"));
        appendInfoLine(startupMessage, "XDir Log", System.getProperty("xdir.log.file"));
        appendInfoLine(startupMessage, "Default Url", System.getProperty("xdir.web.base.url")+"/");
        appendInfoLine(startupMessage, "Admin  Url", System.getProperty("xdir.web.base.secureurl") + "/admin/");
        System.setProperty("xdir.server.started.msg",startupMessage.toString());
    }

    private void appendInfoLine(StringBuilder startupMessage, String header, String value) {
        startupMessage.append("\n");
        startupMessage.append(StringUtils.rightPad(header, 21));
        startupMessage.append("= ");
        startupMessage.append(value);

    }

    public void reload() throws Exception {
        if (logger.isTraceEnabled())
            logger.trace("> reload");

        Session session = jcrFactory.retrieveSession();
        try {
            if (logger.isDebugEnabled())
                logger.debug("loading applications from [{}]", appsRoot);
            Node appsNode = session.getNode(getAppsRoot());
            NodeIterator nodes = appsNode.getNodes();
            while (nodes.hasNext()) {
                Node virtualHostNode = nodes.nextNode();
                registerVirtualHostAwareApp(virtualHostNode);
            }


            StringBuilder appsString = new StringBuilder();
            printAppsTree("root", getRootApplication(), appsString, 0);
            if(logger.isDebugEnabled()) logger.debug("registered apps\n{}", appsString);

            if (logger.isDebugEnabled())
                logger.debug("loaded applications from [{}]", appsRoot);
        } finally {
            session.logout();
        }

        if (logger.isTraceEnabled())
            logger.trace("< reload");
    }

    private void registerVirtualHostAwareApp(Node virtualHostNode) throws Exception{
        if (virtualHostNode.hasNode("app")) {
            scanAppNode(virtualHostNode.getNode("app"), rootApplication);
        }else if(virtualHostNode.hasNode("pages")){
            JcrTemplateAwareApplication parentApp=(JcrTemplateAwareApplication)createApplication(virtualHostNode.getNode("pages"),rootApplication, "defaultApplicationPrototype");
            registerAppToParent(rootApplication,virtualHostNode.getName(), parentApp);
            copyRequiredField(rootApplication, parentApp, "templatePaths");
            if(virtualHostNode.hasNode("static")){
                ResourceApplication staticApp=(ResourceApplication)createApplication(virtualHostNode.getNode("static"),parentApp, "staticResourceServicePrototype");
                staticApp.setTemplatePaths(new String[]{virtualHostNode.getPath()+"/static"});
                staticApp.setName("s");
                parentApp.setResource(staticApp);
                registerAppToParent(parentApp,"s", staticApp);
                copyRequiredField(parentApp, staticApp, "templatePaths");
            }
        }
        
    }

    private void printAppsTree(String appName, Application application, StringBuilder appsString, int level) {
        appsString.append("\n");
        if (level > 0) {
            for (int i = 0; i < level; i++) {
                appsString.append("  ");
            }
        }
        appsString.append(appName);
        for (Map.Entry<String, Application> entry : application.getChildren().entrySet()) {
            printAppsTree(entry.getKey(), entry.getValue(), appsString, level + 1);
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
        if (logger.isTraceEnabled())
            logger.trace("> registerApp({})", appNode.getPath());
        Application application = null;
        if (appNode.hasProperty("_application_bean")) {
            String applicationBeanName = appNode.getProperty("_application_bean").getString();
            application = createApplication(appNode, parentApp, applicationBeanName);
        } else {
            application = new SimplePathApplication();
        }
        
        if (application != rootApplication) {
            if (appNode.hasProperty("_application_virtual_hosts")) {
                String rawVirtualHosts = appNode.getProperty("_application_virtual_hosts").getString();
                rawVirtualHosts = propertiesService.resolvePlaceholders(rawVirtualHosts);
                String[] virtualHosts = rawVirtualHosts.split(",");
                for (String virtualHost : virtualHosts) {
                    registerAppToParent(parentApp, virtualHost.trim(), application);
                }
            } else {
                String appName = appNode.getName();
                if (parentApp==rootApplication && !appNode.getParent().getName().equals("app")) {
                    appName = appNode.getParent().getName();
                }
                registerAppToParent(parentApp, appName, application);
            }
        }

        if (logger.isTraceEnabled())
            logger.trace("< registerApp with {}", application);
        return application;
    }

    private Application createApplication(Node appNode, Application parentApp, String applicationBeanName) throws Exception {
        Application application;
        if (applicationBeanName.indexOf(".") > 0) {
            application = (Application) Class.forName(applicationBeanName).newInstance();
        } else {
            application = beanFactory.getBean(applicationBeanName, Application.class);
        }
        jcrFactory.bindValueToObject(appNode, application, "_application_");

        if (application instanceof ResourceApplication && parentApp instanceof JcrTemplateAwareApplication) {
            ResourceApplication resourceApplication = (ResourceApplication) application;
            resourceApplication.setName(appNode.getName());
            if (!appNode.hasProperty("_application_skip_register_as_parent_resource")) {
                ((JcrTemplateAwareApplication) parentApp).setResource(resourceApplication);
            }
        }

        if (application instanceof JcrTemplateAwareApplication) {
            JcrTemplateAwareApplication nearestJcrTemplateAwareApplication = retrieveNearestJcrTemplateAwareApplication(parentApp);
            JcrTemplateAwareApplication jcrTemplateAwareApplication = (JcrTemplateAwareApplication) application;
            setupJcrBasePath(appNode, parentApp, jcrTemplateAwareApplication);
            if (jcrTemplateAwareApplication.getTemplatePaths()==null && parentApp==rootApplication) {
                jcrTemplateAwareApplication.setTemplatePaths(new String[]{appNode.getParent().getPath() + "/templates"});
                if (logger.isDebugEnabled())
                    logger.debug("detected app {} templates as {}", appNode.getPath(), jcrTemplateAwareApplication.getTemplatePaths()[0]);
            }
            copyRequiredField(nearestJcrTemplateAwareApplication, jcrTemplateAwareApplication, "templatePaths");
        }
        invokeInitIfExist(application);
        if (logger.isDebugEnabled())
            logger.debug("created application [" + applicationBeanName + ":{}] from [{}]", application.getClass().getSimpleName(), appNode.getPath());
        return application;
    }

    private void registerAppToParent(Application parent, String appName, Application application) {
        parent.getChildren().put(appName, application);
        if (application instanceof SimplePathApplication)
            ((SimplePathApplication)application).setParent(parent);
        if (logger.isDebugEnabled()) {
            logger.debug(" register app " + application + " as " + appName + " to parent " + parent);
        }
    }

    private void setupJcrBasePath(Node appNode, Application parentApp, JcrTemplateAwareApplication jcrTemplateAwareApplication) throws RepositoryException {
        if (jcrTemplateAwareApplication.getJcrBasePath() == null) {
            if (parentApp==rootApplication) {
                jcrTemplateAwareApplication.setJcrBasePath(appNode.getParent().getPath() + "/pages");
                if (logger.isDebugEnabled())
                    logger.debug("detected app {} jcr base path as {}", appNode.getPath(), jcrTemplateAwareApplication.getJcrBasePath());
            } else {
                StringBuilder appJcrBasePath = new StringBuilder();
                Node parentNode = appNode;
                while (true) {
                    appJcrBasePath.insert(0, parentNode.getName());
                    appJcrBasePath.insert(0, "/");
                    if (parentApp instanceof JcrTemplateAwareApplication) {
                        appJcrBasePath.insert(0, ((JcrTemplateAwareApplication) parentApp).getJcrBasePath());
                        break;
                    }
                    parentApp = parentApp.getParent();
                    parentNode = parentNode.getParent();
                }
                //
                // /apps/admin/apps/admin/platform/jvm/console
                // /apps/admin/pages/jvm/jvm
                jcrTemplateAwareApplication.setJcrBasePath(appJcrBasePath.toString());
                if (logger.isDebugEnabled())
                    logger.debug("setup app {} jcr base path as {}", appNode.getPath(), appJcrBasePath.toString());
            }
        }
    }

    public PropertiesService getPropertiesService() {
        return propertiesService;
    }

    public void setPropertiesService(PropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

    @Override
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext=bundleContext;
    }
}
