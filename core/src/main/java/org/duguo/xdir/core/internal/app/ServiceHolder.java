package org.duguo.xdir.core.internal.app;

import org.duguo.xdir.spi.model.support.AbstractGetAndPut;
import org.duguo.xdir.spi.service.DynamicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;

import java.util.Map;

public class ServiceHolder extends AbstractGetAndPut implements BeanFactoryAware,InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(ServiceHolder.class);
    private ListableBeanFactory beanFactory;


    @Override
    public void afterPropertiesSet() throws Exception {
        put("beans", beanFactory);
        if (logger.isDebugEnabled())
            logger.debug("registered service beans");
        Map<String,DynamicService> beans = beanFactory.getBeansOfType(DynamicService.class);
        for(DynamicService service:beans.values())
            registerService(service);
    }

    public void onBind(DynamicService service) throws Exception {
        if (logger.isDebugEnabled()) logger.debug("onBind called");
        registerService(service);
    }

    public void onUnbind(DynamicService service) throws Exception {
        if (logger.isDebugEnabled()) logger.debug("onUnbind called: {}", service);
        if (service != null ) {
            getMap().remove(service.getServiceName());
            if (logger.isDebugEnabled())
                logger.debug("de-registered service {}", service.getServiceName());
        }
    }

    private void registerService(DynamicService service) {
        put(service.getServiceName(), service.getServiceInstance());
        if (logger.isDebugEnabled())
            logger.debug("registered service {}", service.getServiceName());
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = (ListableBeanFactory)beanFactory;
    }

}
