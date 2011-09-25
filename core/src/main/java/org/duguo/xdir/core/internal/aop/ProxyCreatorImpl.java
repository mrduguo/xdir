package org.duguo.xdir.core.internal.aop;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;


public class ProxyCreatorImpl implements ProxyCreator,BeanFactoryAware
{
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger( ProxyCreatorImpl.class );
    private BeanFactory beanFactory;
    private String monitorProxyFactoryBeanName;

    public Object createProxy( Object targetObject ) throws Exception
    {
        return createTargetSourceAndProxy( targetObject );
    }


    private synchronized Object createTargetSourceAndProxy( Object targetObject ) throws Exception
    {        
        if(logger.isDebugEnabled())
            logger.debug("create proxy for object [{}]",targetObject.getClass().getName());
        ProxyFactoryBean proxyFactoryBean=beanFactory.getBean("&"+monitorProxyFactoryBeanName, ProxyFactoryBean.class );
        proxyFactoryBean.setTarget( targetObject );
        return proxyFactoryBean.getObject();
    }



    public String getMonitorProxyFactoryBeanName()
    {
        return monitorProxyFactoryBeanName;
    }


    public void setMonitorProxyFactoryBeanName( String monitorProxyFactoryBeanName )
    {
        this.monitorProxyFactoryBeanName = monitorProxyFactoryBeanName;
    }


    public BeanFactory getBeanFactory()
    {
        return beanFactory;
    }


    public void setBeanFactory( BeanFactory beanFactory )
    {
        this.beanFactory = beanFactory;
    }


}
