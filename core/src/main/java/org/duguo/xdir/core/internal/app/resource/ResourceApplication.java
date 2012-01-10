package org.duguo.xdir.core.internal.app.resource;

import org.duguo.xdir.core.internal.app.Application;
import org.duguo.xdir.core.internal.app.JcrTemplateAwareApplication;
import org.duguo.xdir.core.internal.cache.CacheService;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.core.internal.resource.Resource;
import org.duguo.xdir.core.internal.resource.ResourceLoader;
import org.duguo.xdir.util.io.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class ResourceApplication extends JcrTemplateAwareApplication implements ResourceService {
    private static final Logger logger = LoggerFactory.getLogger(ResourceApplication.class);

    private List<String> allResources;
    private ResourceLoader resourceLoader;
    private List<String> templateFormats;
    private String name;
    private CacheService cacheService;
    private String cacheTimestamp;
    private long cacheAgeInHours =8760; // 1 year

    @Override
    public ResourceService getResource() {
        return this;
    }


    protected int handleInSession(ModelImpl model, int handleStatus) throws Exception {
        model.getPathInfo().moveToNextPath(); // skip timestamp in the path
        setupExpireHeader(model);
        String resourcePath = model.getPathInfo().getRemainPath().substring(1);
        String format = null;
        int dotPosition = resourcePath.lastIndexOf('.');
        if (dotPosition > 0) {
            format = resourcePath.substring(dotPosition);
            if (format.indexOf("/") > 0) {
                format = null;
            }
        }
        if (format != null) {
            if (templateFormats.contains(format) || model.getApp().getFormats().containsKey(format)) {
                resourcePath = resourcePath.substring(0, dotPosition);
                model.setFormat(format);

                if (logger.isDebugEnabled())
                    logger.debug("process resource template [{}]", resourcePath);
                handleStatus = getTemplate().process(model, resourcePath);
            } else {
                handleStatus = handleRawResource(model, handleStatus, resourcePath);
            }
        }

        return handleStatus;
    }

    private void setupExpireHeader(ModelImpl model) {
        model.getResponse().setHeader("Cache-Control", "public, max-age=" + (cacheAgeInHours * 3600));
    }


    protected int handleRawResource(ModelImpl model, int handleStatus, String resourcePath) throws Exception {
        Resource resource = resourceLoader.loadResource(model, resourcePath);
        if (resource != null) {
            if (logger.isDebugEnabled())
                logger.debug("write raw resource [{}]", resourcePath);

            InputStream inputStream = resource.getAsInputStream();
            try {
                FileUtil.writeStream(inputStream, model.getResponse().getOutputStream());
                handleStatus = STATUS_SUCCESS;
            } finally {
                inputStream.close();
            }
        }
        return handleStatus;
    }

    public String getResourceUrl(ModelImpl model, String resourceName) {
        StringBuilder resourceUrl = new StringBuilder(model.getPageContext());
        Application appToLookForResource = model.getApp();
        do {
            if (appToLookForResource.getChildren().containsValue(this)) {
                break;
            }
            appToLookForResource = appToLookForResource.getParent();
            moveUrlToUpperLevel(resourceUrl);
        }
        while (true);
        resourceUrl.append("/");
        resourceUrl.append(getName());
        resourceUrl.append("/");
        appendCacheTimestamp(resourceUrl);
        resourceUrl.append("/");
        resourceUrl.append(resourceName);
        return resourceUrl.toString();
    }

    protected void appendCacheTimestamp(StringBuilder resourceUrl) {
        if(cacheTimestamp!=null){
            resourceUrl.append(cacheTimestamp);
            if(cacheService.isCacheExists(resourceUrl.toString())){
                return;
            }else{
                moveUrlToUpperLevel(resourceUrl);
                resourceUrl.append("/");
            }
        }
        cacheTimestamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
        resourceUrl.append(cacheTimestamp);
        cacheService.initCache(resourceUrl.toString());
    }

    private void moveUrlToUpperLevel(StringBuilder resourceUrl) {
        resourceUrl.delete(resourceUrl.lastIndexOf("/"),resourceUrl.length());
    }


    public List<String> getAllResources() {
        return allResources;
    }


    public void setAllResources(List<String> allResources) {
        this.allResources = allResources;
    }


    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    public void setTemplateFormats(List<String> templateFormats) {
        this.templateFormats = templateFormats;
    }

    public void setCacheAgeInHours(long cacheAgeInHours) {
        this.cacheAgeInHours = cacheAgeInHours;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
