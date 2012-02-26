package org.duguo.xdir.core.internal.model;


import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.duguo.xdir.core.internal.app.JcrTemplateAwareApplication;
import org.duguo.xdir.jcr.utils.JcrNodeUtils;

import java.io.InputStream;

public abstract class AbstractJcrModel extends AbstractServletModel {
    private Session session;
    private Node node;
    private Node appNode;
    private JcrTemplateAwareApplication app;

    /**
     * ****************************************************
     * Helper methods
     * *****************************************************
     */

    public String displayPropertyName(String propertyName) {
        return PageHelper.displayPropertyName(propertyName);
    }

    public String displayPropertyValue(String propertyValue) {
        return PageHelper.displayPropertyValue(propertyValue, (ModelImpl) this);
    }

    public boolean isProtectedProperty(String key) {
        return key.startsWith(JcrNodeUtils.JCR_PREFIX);
    }

    public Node getAppNode() throws Exception{
        if(appNode==null)
           appNode=getSession().getNode(app.getJcrBasePath());
        return appNode;
    }

    public boolean streamNodeIfIsFile() throws Exception {
        if ("nt:xfile".equals(JcrNodeUtils.getPropertyIfExist(getNode(), "jcr:primaryType"))) {
            InputStream inputStream = getNode().getNode("jcr:content").getProperty("jcr:data").getBinary().getStream();
            try {
                getResponse().setContentType(getApp().getFormat().resolveContentType(getNode().getName()));
                IOUtils.copy(inputStream, getResponse().getOutputStream());
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
            return true;
        } else
            return false;
    }

    /**
     * ****************************************************
     * Setter and getters
     * *****************************************************
     */

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public JcrTemplateAwareApplication getApp() {
        return app;
    }

    public void setApp(JcrTemplateAwareApplication app) {
        this.app = app;
    }
}
