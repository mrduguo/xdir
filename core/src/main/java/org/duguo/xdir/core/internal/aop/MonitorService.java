package org.duguo.xdir.core.internal.aop;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;


public interface MonitorService
{

    public List<Object[]> monite( String sourceBeanName, DefaultListableBeanFactory beanFactory )throws Exception;

    public List<Object[]> unmonite( String sourceBeanName, DefaultListableBeanFactory beanFactory );

    public Map<Object,List<Object[]>> listMonitors();

    public void clearMonitors();

    public boolean isMonited( String sourceBeanName, DefaultListableBeanFactory beanFactory );
    
    public boolean isMonitable( String sourceBeanName, DefaultListableBeanFactory beanFactory );

}