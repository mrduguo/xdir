package org.duguo.xdir.core.internal.template.freemarker;


import org.duguo.xdir.core.internal.template.TemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.spi.model.Model;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.core.internal.resource.ResourceLoader;


public abstract class AbstractTemplateEngine implements TemplateEngine {

    private static final Logger logger = LoggerFactory.getLogger( AbstractTemplateEngine.class );
    
    private ResourceLoader templateStringLoader;
    
    
    public int process( ModelImpl model,String templateName) throws Exception
    {
        return process( model, templateName,Model.NULL_STRING);
    }

    public void setTemplateStringLoader( ResourceLoader templateStringLoader )
    {
        this.templateStringLoader = templateStringLoader;
    }

    public ResourceLoader getTemplateStringLoader()
    {
        return templateStringLoader;
    }


}
