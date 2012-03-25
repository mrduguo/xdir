package org.duguo.xdir.core.internal.app.resource;

import org.duguo.xdir.core.internal.model.ModelImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CdnResourceApplication extends ResourceApplication {
    private static final Logger logger = LoggerFactory.getLogger(CdnResourceApplication.class);

    public String cdnBaseUrl;
    public boolean cdnEnabled;


    public String getResourceUrl(ModelImpl model, String resourceName) {
        if (model.isCacheableResponse() && cdnEnabled) {
            return buildCdnResourceUrl(resourceName);
        }
        return super.getResourceUrl(model, resourceName);
    }

    private String buildCdnResourceUrl(String resourceName) {
        StringBuilder resourceUrl = new StringBuilder();
        resourceUrl.append(cdnBaseUrl);
        resourceUrl.append("/");
        appendCacheTimestamp(resourceUrl);
        resourceUrl.append("/");
        resourceUrl.append(resourceName);
        if(logger.isDebugEnabled()) logger.debug("built cdn resource url {}",resourceUrl);
        return resourceUrl.toString();
    }

    public String getCdnBaseUrl() {
        return cdnBaseUrl;
    }

    public void setCdnBaseUrl(String cdnBaseUrl) {
        this.cdnBaseUrl = cdnBaseUrl;
    }

    public boolean isCdnEnabled() {
        return cdnEnabled;
    }

    public void setCdnEnabled(boolean cdnEnabled) {
        this.cdnEnabled = cdnEnabled;
    }
}
