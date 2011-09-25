package org.duguo.xdir.core.internal.template;


import org.duguo.xdir.core.internal.model.ModelImpl;

public interface TemplateEngine
{
    public static final String FORMAT_DEFAULT = ".html";
    public static final String TEMPLATE_DEFAULT="index";
    
    int process( ModelImpl model,String templateName) throws Exception;
    
    int process( ModelImpl model,String templateName,String nodeType) throws Exception;
        
    String compress( String format,String rawString) throws Exception;

}
