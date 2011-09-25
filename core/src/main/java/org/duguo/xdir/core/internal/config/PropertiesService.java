package org.duguo.xdir.core.internal.config;


import java.util.Properties;


public interface PropertiesService
{

    public String retriveKeyValue(String key);
    
    public String resolveStringValue( String strVal );
    
    public String resolveToRelativePath(String rawPath);

    public Properties getProperties();

}