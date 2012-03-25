package org.duguo.xdir.core.internal.model;

import org.springframework.beans.factory.FactoryBean;

import java.util.Map;

public class FormatsMap implements FactoryBean<Map<String, String[]>>
{
    private Map<String, String[]> formats;
    
    public Map<String, String[]> getObject() throws Exception
    {
        return formats;
    }

    @SuppressWarnings("unchecked")
    public Class getObjectType()
    {
        return Map.class;
    }

    public boolean isSingleton()
    {
        return true;
    }

    public void setFormats( Map<String, String[]> formats )
    {
        this.formats = formats;
    }

}
