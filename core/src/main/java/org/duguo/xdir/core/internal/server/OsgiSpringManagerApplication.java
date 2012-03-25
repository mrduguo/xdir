package org.duguo.xdir.core.internal.server;


import org.duguo.xdir.core.internal.aop.MonitorService;
import org.duguo.xdir.core.internal.utils.SpringBeanUtil;
import org.eclipse.gemini.blueprint.context.support.AbstractOsgiBundleApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class OsgiSpringManagerApplication extends DefaultAdminApplication implements BeanFactoryAware
{
    
    private static final Logger logger = LoggerFactory.getLogger( OsgiSpringManagerApplication.class );
    
    private BeanFactory beanFactory;
    
    private MonitorService monitorService;
    private String monitorServiceBeanName;
    
    public Map<String,String> generateBeanDefinationXml( String beanName,AbstractOsgiBundleApplicationContext applicationContext ) throws Exception
    {
        Map<String,String> beanDefinationInfo=new HashMap<String, String>();
        String beanXml =null;
        AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition)applicationContext.getBeanFactory().getBeanDefinition( beanName );
        if ( beanDefinition.getResource() != null )
        {
            //load from bean defination resource
            beanXml = SpringBeanUtil.loadBeanDefinationFromXml( beanName, beanDefinition.getResource().getInputStream());
            String configResource=beanDefinition.getResource().getURL().toString();
            beanDefinationInfo.put( "configResource", configResource);
            if(logger.isDebugEnabled())
                logger.debug("loaded bean defination [{}] from resource [{}]",configResource);
        }
        if(beanXml==null){
            // load from locations because local resource doesn't setup in beanDefination
            String[] configLocations=applicationContext.getConfigLocations();
            for(int i=configLocations.length;i>0;i--){
                InputStream xmlResourceStream=readResourceAsStream( configLocations[i-1] );
                if(xmlResourceStream!=null){
                    beanXml=SpringBeanUtil.loadBeanDefinationFromXml(beanName,xmlResourceStream);
                    if(beanXml!=null){
                        beanDefinationInfo.put( "configResource", configLocations[i-1] );
                        if(logger.isDebugEnabled())
                            logger.debug("loaded bean defination [{}] from configuration [{}]",beanName,configLocations[i-1]);
                        break;
                    }                    
                }
            }
        }
        if(beanXml==null){
            // cannot found from any exist resource and generate definition from the beanDefination
            beanXml=SpringBeanUtil.generateXmlFromBeanDefination(beanName,beanDefinition);
            if(logger.isDebugEnabled())
                logger.debug("generated xml from bean defination for bean [{}]",beanName);
        }
        beanDefinationInfo.put( "beanXml", beanXml );
        return beanDefinationInfo;
    }

    public MonitorService retriveMonitorService()
    {
        if(monitorService==null){
            try{
                if(monitorServiceBeanName==null){
                    monitorService=beanFactory.getBean( MonitorService.class );
                }else{
                    monitorService=beanFactory.getBean( monitorServiceBeanName,MonitorService.class );                
                }
            }catch(NoSuchBeanDefinitionException ex){
                // trouble shooting disabled
            }
            
        }
        return monitorService;
    }

    public MonitorService getMonitorService()
    {
        return monitorService;
    }

    public BeanFactory getBeanFactory()
    {
        return beanFactory;
    }

    public void setBeanFactory( BeanFactory beanFactory )
    {
        this.beanFactory = beanFactory;
    }

    public String getMonitorServiceBeanName()
    {
        return monitorServiceBeanName;
    }

    public void setMonitorServiceBeanName( String monitorServiceBeanName )
    {
        this.monitorServiceBeanName = monitorServiceBeanName;
    }

}
