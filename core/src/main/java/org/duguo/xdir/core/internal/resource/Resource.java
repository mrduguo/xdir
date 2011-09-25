package org.duguo.xdir.core.internal.resource;

import java.io.InputStream;

public interface Resource
{

    public String getAsString() throws Exception;

    public InputStream getAsInputStream() throws Exception;

}
