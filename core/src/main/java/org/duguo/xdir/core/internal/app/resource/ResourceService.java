package org.duguo.xdir.core.internal.app.resource;


import org.duguo.xdir.core.internal.model.ModelImpl;

import java.util.List;


public interface ResourceService
{

    public List<String> getAllResources();
    public String getResourceUrl(ModelImpl model,String resourceName);

}
