package org.duguo.xdir.core.internal.app;


import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

import org.duguo.xdir.core.internal.cache.CacheService;
import org.duguo.xdir.spi.model.GetAndPut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.duguo.xdir.spi.security.SecurityService;
import org.duguo.xdir.core.internal.app.resource.ResourceService;
import org.duguo.xdir.core.internal.config.PropertiesService;
import org.duguo.xdir.core.internal.exception.ResourceNotFoundException;
import org.duguo.xdir.core.internal.jcr.JcrFactory;
import org.duguo.xdir.core.internal.jcr.JcrService;
import org.duguo.xdir.core.internal.jcr.SessionCallback;
import org.duguo.xdir.core.internal.model.FormatService;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.core.internal.site.Site;
import org.duguo.xdir.core.internal.template.TemplateEngine;
import org.duguo.xdir.core.internal.utils.RequestUtils;
import org.duguo.xdir.http.client.HttpClientService;
import org.duguo.xdir.util.http.HttpUtil;


public class JcrTemplateAwareApplication extends SimplePathApplication implements ApplicationContextAware{
    private static final Logger logger = LoggerFactory.getLogger(JcrTemplateAwareApplication.class);


    private GetAndPut service;
    private ApplicationContext applicationContext;
    private TemplateEngine template;
    private FormatService format;
    private JcrFactory jcrFactory;
    private ResourceService resource;
    private PropertiesService propertiesService;

    private String jcrBasePath;
    private Map<String, String[]> formats;
    private boolean supportMultipleFormat=true;
    private String[] templatePaths;


    public int execute(ModelImpl model) throws Exception {
        if (logger.isTraceEnabled())
            logger.trace("> execute {} {}", model.getPathInfo().getCurrentAppPath(), model.getPathInfo().getCurrentPath());
        model.setApp(this);
        int handleStatus = STATUS_PAGE_NOT_FOUND;
        try {
            jcrFactory.retrieveSession(model);
            handleStatus = handleInSession(model, handleStatus);
        } catch (ResourceNotFoundException ex) {
        } catch (Throwable ex) {
            handleStatus = handleInternalException(model, STATUS_INTERNAL_ERROR, ex);
        } finally {
            if (handleStatus == STATUS_PAGE_NOT_FOUND) {
                handleStatus = handlePageNotFound(model);
            }
            closeSessionIfAlreadyOpen(model);
        }
        if (logger.isTraceEnabled()) logger.trace("< execute {}", handleStatus);
        return handleStatus;
    }

    public void doInDefaultSession(SessionCallback sessionCallback) {
        try {
            Session session = getJcrFactory().retrieveSession();
            Assert.notNull(session);
            try {
                sessionCallback.execute(session);
            } finally {
                if (session != null) {
                    session.logout();
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("failed to execute session callback", ex);
        }
    }

    protected int handleInSession(ModelImpl model, int handleStatus) throws Exception {
        if (logger.isTraceEnabled()) logger.trace("> handleInSession");
        if(supportMultipleFormat)
            format.resolveFormat(model);
        if (resolveNode(model) != null) {
            setupAction(model);
            handleStatus = processTemplate(model);
        }
        if (logger.isTraceEnabled()) logger.trace("< handleInSession {}", handleStatus);
        return handleStatus;
    }

    protected int processTemplate(ModelImpl model) throws Exception {
        int handleStatus = getTemplate().process(model, TemplateEngine.TEMPLATE_DEFAULT, model.getNodeType());
        return handleStatus;
    }

    protected Node resolveNode(ModelImpl model) throws Exception {
        return jcrFactory.retriveNode(model);
    }

    protected void setupAction(ModelImpl model) {
        String action = RequestUtils.retriveStringParameter(model, "action", null);
        if (action == null) {
            action = model.getRequest().getMethod();
            if (HttpUtil.METHOD_GET.equals(action)) {
                // ignore get method
                return;
            } else {
                action = action.toLowerCase();
            }
        }
        model.setAction(action);
    }


    protected void closeSessionIfAlreadyOpen(ModelImpl model) {
        if (model.getSession() != null) {
            model.getSession().logout();
        }
    }

    protected int handleErrorCode(ModelImpl model, int handleStatus) {
        try {
            model.getResponse().setStatus(handleStatus);
            getTemplate().process(model, String.valueOf(handleStatus));
            return STATUS_SUCCESS;
        } catch (ResourceNotFoundException ex) {
            // ignore and return original status
        } catch (Exception ex) {
            logger.error("failed to handle status code [" + handleStatus + "]", ex);
        }
        return handleStatus;
    }



    /**
     * ****************************************************
     * default getter and setter
     * *****************************************************
     */

    public TemplateEngine getTemplate() {
        return template;
    }


    public void setTemplate(TemplateEngine template) {
        this.template = template;
    }


    public FormatService getFormat() {
        return format;
    }


    public void setFormat(FormatService format) {
        this.format = format;
    }


    public JcrFactory getJcrFactory() {
        return jcrFactory;
    }


    public void setJcrFactory(JcrFactory jcrFactory) {
        this.jcrFactory = jcrFactory;
    }


    public ResourceService getResource() {
        if (resource == null) {
            Application parent = getParent();
            while (true) {
                if (parent instanceof JcrTemplateAwareApplication) {
                    resource = ((JcrTemplateAwareApplication) parent).getResource();
                    break;
                }else{
                    parent=parent.getParent();
                }
            }
        }
        return resource;
    }


    public void setResource(ResourceService resource) {
        this.resource = resource;
    }


    public String[] getTemplatePaths() {
        return templatePaths;
    }


    public void setTemplatePaths(String[] templatePaths) {
        ensureEndWithSlashForTemplatePaths(templatePaths);
        this.templatePaths = templatePaths;
    }

    public Map<String, String[]> getFormats() {
        return formats;
    }


    public void setFormats(Map<String, String[]> formats) {
        this.formats = formats;
    }


    public String getJcrBasePath() {
        return jcrBasePath;
    }

    public void setJcrBasePath(String jcrBasePath) {
        this.jcrBasePath = jcrBasePath;
    }



    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private void ensureEndWithSlashForTemplatePaths(String[] templatePaths) {
        for (int i = 0; i < templatePaths.length; i++) {
            String templatePath = templatePaths[i].trim();
            if (!templatePath.endsWith("/")) {
                templatePath = templatePath + "/";
            }
            templatePaths[i] = templatePath;
        }
    }

    public boolean isSupportMultipleFormat() {
        return supportMultipleFormat;
    }

    public void setSupportMultipleFormat(boolean supportMultipleFormat) {
        this.supportMultipleFormat = supportMultipleFormat;
    }

    public GetAndPut getService() {
        return service;
    }

    public void setService(GetAndPut service) {
        this.service = service;
    }

    public PropertiesService getPropertiesService() {
        return propertiesService;
    }

    public void setPropertiesService(PropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }
}
