package org.duguo.xdir.core.internal.resource;


import org.duguo.xdir.core.internal.model.ModelImpl;

public interface ResourceLoader {
    
    public Resource loadResource( ModelImpl model, String resourcePath ) throws Exception;
    
    public String load( ModelImpl model, String templateName );

    public String load( ModelImpl model , String templateName, String nodeType);

    public String load( ModelImpl model,  String templateName, String nodeType,String format );
}
