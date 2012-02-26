package org.duguo.xdir.service.codec.freemarker;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.duguo.xdir.spi.service.DynamicService;
import org.duguo.xdir.spi.service.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.Hashtable;

public class FtlCodec implements StringCodec ,DynamicService
{
    private static final Logger logger = LoggerFactory.getLogger(FtlCodec.class);

    @Override
    public String apply(String input) throws Exception{
        return apply(input,new Hashtable());
    }

    @Override
    public String apply(String input, Object model) throws Exception{
        if(logger.isTraceEnabled()) logger.trace("> apply {}",input);
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        String stringTemplate = "stringTemplate";
        stringLoader.putTemplate(stringTemplate, input);
        Configuration cfg = new Configuration();
        cfg.setTemplateLoader(stringLoader);
        StringWriter result = new StringWriter();
        try{
            Template templateObj = cfg.getTemplate(stringTemplate);
            templateObj.process(model, result);
        }catch (Exception ex){
            throw new RuntimeException("failed to render free marker template: "+input,ex);
        }
        if(logger.isTraceEnabled()) logger.trace("< apply {}",result);
        return result.toString();
    }

    @Override
    public Object getServiceInstance() {
        return this;
    }

    @Override
    public String getServiceName() {
        return System.getProperty( "xdir.service.codec.ftl.service.name", "ftlCodec");
    }
}
