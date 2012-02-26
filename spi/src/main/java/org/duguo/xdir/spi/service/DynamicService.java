package org.duguo.xdir.spi.service;


/**
 * service will be available for xdir context as app.service.SERVICE_NAME
 */
public interface DynamicService
{
    Object getServiceInstance();

    public String getServiceName();
    
}
