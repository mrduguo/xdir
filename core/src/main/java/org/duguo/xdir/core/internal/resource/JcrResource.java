package org.duguo.xdir.core.internal.resource;

import org.duguo.xdir.jcr.utils.JcrNodeUtils;

import javax.jcr.Property;
import java.io.InputStream;

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
