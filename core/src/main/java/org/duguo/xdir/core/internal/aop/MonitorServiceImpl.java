package org.duguo.xdir.core.internal.aop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.Assert;
import org.duguo.xdir.spi.util.bean.BeanUtil;

public class MonitorServiceImpl implements MonitorService
{   
    private static final Logger logger = LoggerFactory.getLogger( MonitorServiceImpl.class );
    private Map<Object,List<Object[]>> allMonitoredDependencies=new HashMap<Object, List<Object[]>>();
    private ProxyCreator proxyCreator;
    
    public List<Object[]> monite(String sourceBeanName,DefaultListableBeanFactory beanFactory) throws Exception{
        Assert.state( beanFactory.isSingleton( sourceBeanName ) );
        String[] dependentBeans=beanFactory.getDependentBeans( sourceBeanName );
        List<Object[]> monitoredDependencies=new ArrayList<Object[]>();
        if(dependentBeans.length>0){
            Object sourceBean=beanFactory.getBean( sourceBeanName );
            Object proxyBean=proxyCreator.createProxy( sourceBean);
            for(String currentBeanName:dependentBeans){
                if(beanFactory.isSingleton( currentBeanName ) && currentBeanName.charAt( 0 )!='&'){
                    Object targetBean=beanFactory.getBean( currentBeanName );
                    try{
                        if(BeanUtil.bindFieldValueIfHasSetter( targetBean, proxyBean )){
                            monitoredDependencies.add(new Object[]{currentBeanName, targetBean} );
                            if(logger.isDebugEnabled())
                                logger.debug("monitored target [{}]",targetBean.getClass().getName());
                        }
                    }catch(Exception ex){
                        logger.warn( "failed to monitor object",ex );
                    }
                }
            }
            if(monitoredDependencies.size()>0){
                allMonitoredDependencies.put( sourceBean, monitoredDependencies );
            }
        }
        return monitoredDependencies;
    }
    public boolean isMonitable( String sourceBeanName, DefaultListableBeanFactory beanFactory ){
        String[] dependentBeans=beanFactory.getDependentBeans( sourceBeanName );
        if(beanFactory.isSingleton( sourceBeanName ) && dependentBeans.length>0){
            Object sourceBean=beanFactory.getBean( sourceBeanName );
            for(String currentBeanName:dependentBeans){
                if(beanFactory.isSingleton( currentBeanName ) && currentBeanName.charAt( 0 )!='&'){
                    Object targetBean=beanFactory.getBean( currentBeanName );
                    try{
                        if(BeanUtil.bindFieldValueIfHasSetter( targetBean, sourceBean )){
                            if(logger.isDebugEnabled())
                                logger.debug("bean [{}] is monitable",sourceBeanName);
                            return true;
                        }
                    }catch(Exception ex){
                        logger.warn( "failed to test bean monitable flag",ex );
                    }
                }
            }
        }
        return false;
    }
    
    public List<Object[]> unmonite(String sourceBeanName,DefaultListableBeanFactory beanFactory){
        Assert.state( beanFactory.isSingleton( sourceBeanName ) );
        Object sourceBean=beanFactory.getBean( sourceBeanName );
        List<Object[]> monitoredDependencies=null;
        if(sourceBean!=null){
            monitoredDependencies=allMonitoredDependencies.get( sourceBean );
            if(monitoredDependencies!=null){
                for(Object[] targetNameAndBean:monitoredDependencies){
                    try{
                        BeanUtil.bindFieldValueIfHasSetter( targetNameAndBean[1], sourceBean );
                        if(logger.isDebugEnabled())
                            logger.debug("unmonited target [{}]",targetNameAndBean[0]);
                    }catch(Exception ex){
                        logger.warn( "failed to unmonitor object",ex );
                    }
                }
                allMonitoredDependencies.remove( sourceBean );
            }
        }      
        return monitoredDependencies;
    }
    
    public Map<Object,List<Object[]>> listMonitors(){
        return allMonitoredDependencies;
    }
    
    public void clearMonitors(){
        for(Object sourceBean:allMonitoredDependencies.keySet()){
            List<Object[]> monitoredDependencies=allMonitoredDependencies.get( sourceBean );
            if(monitoredDependencies!=null){
                for(Object[] targetNameAndBean:monitoredDependencies){
                    try{
                        BeanUtil.bindFieldValueIfHasSetter( targetNameAndBean[1], sourceBean );
                        if(logger.isDebugEnabled())
                            logger.debug("unmonited target [{}]",targetNameAndBean[0]);
                    }catch(Exception ex){
                        logger.warn( "failed to unmonitor object",ex );
                    }
                }
                allMonitoredDependencies.remove( sourceBean );
            }            
        }        
    }
    
    public boolean isMonited(String sourceBeanName,DefaultListableBeanFactory beanFactory){
        if(beanFactory.isSingleton( sourceBeanName )){
            Object sourceBean=beanFactory.getBean( sourceBeanName );
            if(sourceBean!=null){
                return allMonitoredDependencies.containsKey( sourceBean );
            }   
        }
        return false;
    }

    public ProxyCreator getProxyCreator()
    {
        return proxyCreator;
    }

    public void setProxyCreator( ProxyCreator proxyCreator )
    {
        this.proxyCreator = proxyCreator;
    }
}
