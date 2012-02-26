package org.duguo.xdir.core.internal.config;

import java.util.Map;
import java.util.Properties;

import org.duguo.xdir.spi.service.DynamicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.StringValueResolver;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;
import org.duguo.xdir.util.bean.BeanUtil;

public class PropertiesServiceImpl extends PropertyPlaceholderConfigurer implements PropertiesService,DynamicService
{
    private static final Logger logger = LoggerFactory.getLogger( PropertiesServiceImpl.class );
    private Properties mergedProperties;
    private StringValueResolver valueResolver;
    
    public String resolveStringValue(String strVal) {
        String value=valueResolver.resolveStringValue( strVal );
        if(logger.isDebugEnabled())
            logger.debug( "resolved string [{}] as [{}]",strVal,value );
        return value;
    }
    
    public String retriveKeyValue(String key) {
        String value=System.getProperty( key );
        if(value==null){
            value=mergedProperties.getProperty( key );
        }
        if(logger.isDebugEnabled())
            logger.debug( "retrived key value [{}] as [{}]",key,value );
        return value;
    }
        
    public Properties getProperties(){
        return mergedProperties;
    }

    public String resolveToRelativePath(String rawPath){
        String relativePath=rawPath;
        relativePath = resolvePathFromProperties( rawPath, relativePath, System.getProperties() );
        relativePath = resolvePathFromProperties( rawPath, relativePath, mergedProperties );
        if(logger.isDebugEnabled())
            logger.debug( "resolved [{}] as relative path [{}]",rawPath,relativePath );
        return relativePath;
    }

    @SuppressWarnings("unchecked")
    private String resolvePathFromProperties( String rawPath, String relativePath, Properties properties )
    {
        for(Map.Entry entry:properties.entrySet()){
            String key=(String)entry.getKey();
            if(key.startsWith( "xdir.dir." )){
                String basePath=(String)entry.getValue();
                int splitIndex=rawPath.indexOf(basePath);
                if(splitIndex>=0){
                    String newRelativePath=rawPath.substring( 0,splitIndex )+"${"+key+"}"+rawPath.substring(splitIndex+basePath.length() );
                    if(newRelativePath.length()<relativePath.length()){
                        relativePath=newRelativePath;
                    }                    
                }
            }
        }
        return relativePath;
    }
    

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
            throws BeansException {
        mergedProperties=props;
        valueResolver = new PlaceholderResolvingStringValueResolver(props);
        super.processProperties( beanFactoryToProcess, props );
        if(logger.isDebugEnabled())
            logger.debug( "PropertiesService inited" );
    }

    @Override
    public Object getServiceInstance() {
        return this;
    }

    @Override
    public String getServiceName() {
        return System.getProperty("xdir.service.props.service.name","props");
    }


    /**
     * Copy from PropertyPlaceholderConfigurer due to private inner class
     * 
     * @see org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
     */
    protected class PlaceholderResolvingStringValueResolver implements StringValueResolver {

        private final PropertyPlaceholderHelper helper;

        private final PlaceholderResolver resolver;
        
        private String nullValue;

        public PlaceholderResolvingStringValueResolver(Properties props) {
            ;
            this.helper = new PropertyPlaceholderHelper(
                BeanUtil.retriveFieldValue( PropertiesServiceImpl.this, String.class, "placeholderPrefix" ),
                BeanUtil.retriveFieldValue( PropertiesServiceImpl.this, String.class, "placeholderSuffix" ),
                BeanUtil.retriveFieldValue( PropertiesServiceImpl.this, String.class, "valueSeparator" ),
                BeanUtil.retriveFieldValue( PropertiesServiceImpl.this, Boolean.class, "ignoreUnresolvablePlaceholders" ));
            this.resolver = new PropertyPlaceholderConfigurerResolver(props);
            this.nullValue=BeanUtil.retriveFieldValue( PropertiesServiceImpl.this, String.class, "nullValue" );
        }

        public String resolveStringValue(String strVal) throws BeansException {
            String value = this.helper.replacePlaceholders(strVal, this.resolver);
            return (value.equals(nullValue) ? null : value);
        }
    }


    protected class PropertyPlaceholderConfigurerResolver implements PlaceholderResolver {

        private final Properties props;

        private PropertyPlaceholderConfigurerResolver(Properties props) {
            this.props = props;
        }

        public String resolvePlaceholder(String placeholderName) {
            return PropertiesServiceImpl.this.resolvePlaceholder(placeholderName, props, BeanUtil.retriveFieldValue( PropertiesServiceImpl.this, Integer.class, "systemPropertiesMode" ));
        }
    }
}
