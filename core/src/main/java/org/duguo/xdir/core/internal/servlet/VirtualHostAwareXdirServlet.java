package org.duguo.xdir.core.internal.servlet;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.util.http.HttpUtil;


public class VirtualHostAwareXdirServlet extends XdirServletImpl implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(VirtualHostAwareXdirServlet.class);
    private Set<String> defaultHostUrls;
    private Set<String> supportedVirtualHostUrls;


    public void afterPropertiesSet() throws Exception {
        Assert.notNull(defaultHostUrls);
        normalizeUrls(defaultHostUrls);
        if (supportedVirtualHostUrls != null) {
            normalizeUrls(supportedVirtualHostUrls);
        }
    }


    private void normalizeUrls(Set<String> urls) {
        Set<String> normalizedUrls = new HashSet<String>();
        for (String rawUrl : urls) {
            normalizedUrls.add(HttpUtil.normalizeUrl(rawUrl));
        }
        urls.clear();
        urls.addAll(normalizedUrls);
    }


    protected String buildPath(HttpServletRequest request, ModelImpl model) throws Exception {
        StringBuffer hostUrl = new StringBuffer(48);
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int port = request.getServerPort();

        hostUrl.append(scheme);
        hostUrl.append("://");
        hostUrl.append(serverName);
        if (port > 0 &&
                ((scheme.equalsIgnoreCase(HttpUtil.HTTP) && port != 80) ||
                        (scheme.equalsIgnoreCase(HttpUtil.HTTPS) && port != 443))) {
            hostUrl.append(':');
            hostUrl.append(port);
        }

        String virtualHostUrl = hostUrl.toString();
        if (logger.isDebugEnabled())
            logger.debug("resolved virtual host url [{}]", virtualHostUrl);
        model.setHostUrl(virtualHostUrl);
        if (!defaultHostUrls.contains(virtualHostUrl)) {
            if (supportedVirtualHostUrls != null && !supportedVirtualHostUrls.contains(virtualHostUrl)) {
                return null;
            }
            model.setVirtualHostPath(scheme + "_" + serverName + "_" + port);
            String path = "virtualhosts/" + scheme + "_" + serverName.replaceAll("\\.","_") + "_" + port + "/" + super.buildPath(request, model);
            if (logger.isDebugEnabled())
                logger.debug("resolved virtual host path [{}]", path);
            return path;
        } else {
            return super.buildPath(request, model);
        }
    }


    public Set<String> getSupportedVirtualHostUrls() {
        return supportedVirtualHostUrls;
    }


    public void setSupportedVirtualHostUrls(Set<String> supportedVirtualHostUrls) {
        this.supportedVirtualHostUrls = supportedVirtualHostUrls;
    }


    public Set<String> getDefaultHostUrls() {
        return defaultHostUrls;
    }


    public void setDefaultHostUrls(Set<String> defaultHostUrls) {
        this.defaultHostUrls = defaultHostUrls;
    }
}
