package org.duguo.xdir.core.internal.resource;

import java.io.InputStream;

import javax.jcr.Property;

import org.duguo.xdir.core.internal.utils.JcrNodeUtils;

public class JcrResource implements Resource
{
    
    Property dataProperty;
    
    public JcrResource(Property dataProperty){
        this.dataProperty=dataProperty;
    }

    public String getAsString() throws Exception
    {
        return JcrNodeUtils.getPropertyStringValue(dataProperty);
    }

    public InputStream getAsInputStream() throws Exception
    {
        return dataProperty.getValue().getBinary().getStream();
    }
}
