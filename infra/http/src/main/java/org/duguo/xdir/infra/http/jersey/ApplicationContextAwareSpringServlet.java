package org.duguo.xdir.infra.http.jersey;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.servlet.WebConfig;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.ws.rs.core.MediaType;
import java.util.Map;


public class ApplicationContextAwareSpringServlet extends SpringServlet implements ApplicationContextAware{

    private ApplicationContext applicationContext;
    private DefaultResourceConfig defaultResourceConfig;
    private Map<String, MediaType> mediaExtentions;

    @Override
    protected ResourceConfig getDefaultResourceConfig(Map<String, Object> props,
                                                      WebConfig webConfig) throws ServletException {
        if(mediaExtentions!=null)
            defaultResourceConfig.getMediaTypeMappings().putAll(mediaExtentions);
        return defaultResourceConfig;
    }

    @Override
    protected ConfigurableApplicationContext getContext() {
        return (ConfigurableApplicationContext)applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    public void setDefaultResourceConfig(DefaultResourceConfig defaultResourceConfig) {
        this.defaultResourceConfig = defaultResourceConfig;
    }

    public void setMediaExtentions(Map<String, MediaType> mediaExtentions) {
        this.mediaExtentions = mediaExtentions;
    }
}
