package org.duguo.xdir.core.internal.config;

import org.duguo.xdir.spi.service.DynamicService;
import org.duguo.xdir.spi.util.bean.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;
import org.springframework.util.StringValueResolver;
import org.springframework.util.SystemPropertyUtils;

import java.util.Properties;

public class PropertiesServiceImpl implements PropertiesService,DynamicService
{
    private static final Logger logger = LoggerFactory.getLogger( PropertiesServiceImpl.class );

    public String resolvePlaceholders(String strVal) {
        String value= SystemPropertyUtils.resolvePlaceholders(strVal);
        if(logger.isDebugEnabled()) logger.debug( "resolved string [{}] as [{}]",strVal,value );
        return value;
    }

    public String retrieveKeyValue(String key) {
        String value=System.getProperty( key );
        if(logger.isDebugEnabled()) logger.debug( "retrieved key value [{}] as [{}]",key,value );
        return value;
    }

    public String resolveToRelativePath(String rawPath){
        String relativePath=rawPath;
        String xdirHome=System.getProperty("xdir.home");
        if(rawPath.startsWith(xdirHome)){
            relativePath="${xdir.home}"+rawPath.substring(xdirHome.length());
        }
        if(logger.isDebugEnabled()) logger.debug( "resolved [{}] as relative path [{}]",rawPath,relativePath );
        return relativePath;
    }

    @Override
    public Object getServiceInstance() {
        return this;
    }

    @Override
    public String getServiceName() {
        return System.getProperty("xdir.service.props.service.name","props");
    }
}
