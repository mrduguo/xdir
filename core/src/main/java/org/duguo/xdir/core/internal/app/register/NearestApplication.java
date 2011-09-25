package org.duguo.xdir.core.internal.app.register;

import org.duguo.xdir.core.internal.app.JcrTemplateAwareApplication;

public class NearestApplication
{
    private JcrTemplateAwareApplication application;
    private String relativePath;
    
    public JcrTemplateAwareApplication getApplication()
    {
        return application;
    }
    public void setApplication( JcrTemplateAwareApplication application )
    {
        this.application = application;
    }
    public String getRelativePath()
    {
        return relativePath;
    }
    public void setRelativePath( String relativePath )
    {
        this.relativePath = relativePath;
    }
    
}
