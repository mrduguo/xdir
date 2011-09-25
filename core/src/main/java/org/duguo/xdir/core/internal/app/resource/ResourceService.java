package org.duguo.xdir.core.internal.app.resource;


import java.util.List;

import org.duguo.xdir.core.internal.model.ModelImpl;


public interface ResourceService
{

    public List<String> getAllResources();
    public String getResourceUrl(ModelImpl model,String resourceName);

}
