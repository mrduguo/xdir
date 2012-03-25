package org.duguo.xdir.http.jersey;

import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;


public class OsgiContextAwareSpringServlet extends SpringServlet implements ApplicationContextAware{

    private ApplicationContext applicationContext;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        super.init(config);
        Thread.currentThread().setContextClassLoader(previousClassLoader);
    }

    @Override
    protected ConfigurableApplicationContext getContext() {
        return (ConfigurableApplicationContext)applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
