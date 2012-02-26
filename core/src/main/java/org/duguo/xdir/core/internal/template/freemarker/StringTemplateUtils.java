package org.duguo.xdir.core.internal.template.freemarker;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.duguo.xdir.spi.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class StringTemplateUtils {
    private static final Logger logger = LoggerFactory.getLogger(StringTemplateUtils.class);

    public static String render(String template, Object model) {
        if (logger.isTraceEnabled()) logger.trace("> render with template: {}", template);
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        String stringTemplate = "stringTemplate";
        stringLoader.putTemplate(stringTemplate, template);
        Configuration cfg = new Configuration();
        cfg.setTemplateLoader(stringLoader);
        StringWriter result = new StringWriter();
        try{
            Template templateObj = cfg.getTemplate(stringTemplate);
            templateObj.process(model, result);
        }catch (Exception ex){
             throw new RuntimeException("failed to render free marker template: "+template,ex);
        }
        if (logger.isTraceEnabled()) logger.trace("< render with result: {}", result.toString());
        return result.toString();
    }
}
