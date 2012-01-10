package org.duguo.xdir.core.internal.model;


import javax.jcr.Node;
import javax.jcr.Session;

import org.duguo.xdir.core.internal.app.JcrTemplateAwareApplication;
import org.duguo.xdir.jcr.utils.JcrNodeUtils;

public abstract class AbstractJcrModel extends AbstractServletModel
{
    private Session session;
    private Node node;
    private JcrTemplateAwareApplication app;

    /*******************************************************
     * Helper methods
     *******************************************************/

    public String displayPropertyName(String propertyName) {
        return PageHelper.displayPropertyName(propertyName);
    }
    public String displayPropertyValue(String propertyValue) {
        return PageHelper.displayPropertyValue(propertyValue,(ModelImpl)this);
    }
    
    public boolean isProtectedProperty(String key){
        return key.startsWith( JcrNodeUtils.JCR_PREFIX);
    }

    /*******************************************************
     * Setter and getters
     *******************************************************/
    
    public Node getNode()
    {
        return node;
    }
    public void setNode( Node node )
    {
        this.node = node;
    }
    public void setSession( Session session )
    {
        this.session = session;
    }
    public Session getSession()
    {
        return session;
    }
    public JcrTemplateAwareApplication getApp()
    {
        return app;
    }
    public void setApp( JcrTemplateAwareApplication app )
    {
        this.app = app;
    }
}
