package org.duguo.xdir.core.internal.config;

public interface PropertiesService
{

    public String retrieveKeyValue(String key);
    
    public String resolvePlaceholders( String strVal );
    
    public String resolveToRelativePath(String rawPath);

}