package org.duguo.xdir.core.internal.template.freemarker;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.core.internal.app.Application;
import org.duguo.xdir.core.internal.exception.ResourceNotFoundException;
import org.duguo.xdir.core.internal.model.ModelImpl;
import org.duguo.xdir.core.internal.template.AbstractTemplateEngine;
import org.duguo.xdir.core.internal.template.BufferedResponse;

import freemarker.core.StopException;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;


public class FreeMarkerTemplateEngine extends AbstractTemplateEngine {

    private static final Logger logger = LoggerFactory.getLogger( FreeMarkerTemplateEngine.class );
    
    public ObjectWrapper objectWrapper= new DefaultObjectWrapper();


    public int process( ModelImpl model,String templateName,String nodeType) throws Exception
    {
        try {
            Configuration configuration = new Configuration();
            configuration.setTemplateExceptionHandler( TemplateExceptionHandler.RETHROW_HANDLER );
            configuration.setLocalizedLookup(false);
            configuration.setObjectWrapper(objectWrapper);
            configuration.setTemplateLoader(new FreeMarkerJcrTemplateLoader(getTemplateStringLoader(),model,nodeType));
            Template template = configuration.getTemplate(templateName);
            BufferedResponse bufferedResponse=new BufferedResponse( model.getResponse() );
            model.setResponse( bufferedResponse );
            template.process(model, bufferedResponse.getWriter());
            return Application.STATUS_SUCCESS;
        }catch(ResourceNotFoundException ex) {
            throw ex;
        }catch(StopException ignore) {
            if(logger.isDebugEnabled())
                logger.debug("template engine stopped process with message [{}]",ignore.getMessage());
            return Application.STATUS_SUCCESS;
        }
    }

}
