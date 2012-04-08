package org.duguo.xdir.infra.spring.config;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.*;
import org.springframework.util.SystemPropertyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class EnvironmentPropertiesPlaceholderConfigurer extends PropertySourcesPlaceholderConfigurer implements EnvironmentAware, BeanNameAware {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private String selfBeanName;
    private Environment environment;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        copyEnvironmentToSystemProperties();
        collectAllConfiguredPropertiesAndStoreInSystemProperties(beanFactory);
        logSystemPropertiesWhenDebug();
        performPostProcessBeanFactory(beanFactory);
    }

    private void logSystemPropertiesWhenDebug() {
        if(logger.isDebugEnabled()){
            List<String> keys=new ArrayList(System.getProperties().keySet());
            Collections.sort(keys);
            for(String key:keys)
                logger.debug("system property ["+key+"="+System.getProperty(key)+"]");
        }
    }

    private void copyEnvironmentToSystemProperties() {
        for(Map.Entry<String,String> entry:System.getenv().entrySet()){
             if(System.getProperty(entry.getKey())==null){
                  System.setProperty(entry.getKey(),entry.getValue());
                 if(logger.isDebugEnabled()) logger.debug("copied environment variable to system properties ["+entry.getKey()+"="+entry.getValue()+"]");
             }
        }
    }

    /**
     * @see org.springframework.beans.factory.config.PropertyResourceConfigurer#order
     *      default order is at lowest priority Ordered.LOWEST_PRECEDENCE / Integer.MAX_VALUE
     */
    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanName(String beanName) {
        selfBeanName = beanName;
    }

    private void performPostProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        MutablePropertySources mutablePropertySources;
        if (environment instanceof AbstractEnvironment) {
            mutablePropertySources = ((AbstractEnvironment) environment).getPropertySources();
        } else {
            mutablePropertySources = new MutablePropertySources();
            mutablePropertySources.addLast(
                    new PropertySource<Environment>(ENVIRONMENT_PROPERTIES_PROPERTY_SOURCE_NAME, this.environment) {
                        @Override
                        public String getProperty(String key) {
                            return this.source.getProperty(key);
                        }
                    }
            );
        }
        this.processProperties(beanFactory, new PropertySourcesPropertyResolver(mutablePropertySources));
    }

    private void collectAllConfiguredPropertiesAndStoreInSystemProperties(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Properties mergedProperties = collectProperties(beanFactory);
        List<String> keysNeedResolve = new ArrayList<String>();
        for (Map.Entry entry : mergedProperties.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            if (System.getProperty(key) == null) {
                System.setProperty(key, value);
                if (logger.isDebugEnabled()) logger.debug("Added new property [{}={}]", key, value);
                if (value.contains(SystemPropertyUtils.PLACEHOLDER_PREFIX)) {
                    if (logger.isDebugEnabled()) logger.debug("Key need to be resolved [{}]", key);
                    keysNeedResolve.add(key);
                }
            } else {
                if (logger.isDebugEnabled()) logger.debug("Ignore existing property [{}={}]", key, value);
            }
        }

        resolveKeys(keysNeedResolve);
    }

    private Properties collectProperties(ConfigurableListableBeanFactory beanFactory) {
        String[] placeHolderConfigurers = beanFactory.getBeanNamesForType(PlaceholderConfigurerSupport.class);
        Properties inlineProperties = new Properties();
        Properties locationsProperties = new Properties();
        for (String configBeanName : placeHolderConfigurers) {
            if (!configBeanName.equals(selfBeanName)) {
                collectSingleBeanProperties(beanFactory, inlineProperties, locationsProperties, configBeanName);

            }
        }
        collectSingleBeanProperties(beanFactory, inlineProperties, locationsProperties, selfBeanName);

        inlineProperties.putAll(locationsProperties);
        return inlineProperties;
    }

    private void resolveKeys(List<String> keysNeedResolve) {
        List<String> keysStillNeedResolve = new ArrayList<String>();
        IllegalArgumentException lastResolveFailure = null;
        for (String key : keysNeedResolve) {
            try {
                System.setProperty(key, SystemPropertyUtils.resolvePlaceholders(System.getProperty(key)));
                if (logger.isDebugEnabled()) logger.debug("Resolved key [{}]", key);
            } catch (IllegalArgumentException ex) {
                keysStillNeedResolve.add(key);
                lastResolveFailure = ex;
            }
        }

        if (keysStillNeedResolve.size() > 0) {
            if (keysStillNeedResolve.size() != keysNeedResolve.size())
                resolveKeys(keysStillNeedResolve);
            else
                throw lastResolveFailure;
        }
    }

    private void collectSingleBeanProperties(ConfigurableListableBeanFactory beanFactory, Properties inlineProperties, Properties locationsProperties, String configBeanName) throws BeansException {
        if (logger.isTraceEnabled()) logger.trace("> collectSingleBeanProperties with bean [{}]", configBeanName);
        BeanDefinition placeHolderBeanDefinition = beanFactory.getBeanDefinition(configBeanName);
        MutablePropertyValues propertyValues = placeHolderBeanDefinition.getPropertyValues();
        processInlineProperties(beanFactory, inlineProperties, propertyValues);
        processLocationsProperties(beanFactory, locationsProperties, propertyValues);
        if (logger.isTraceEnabled()) logger.trace("< collectSingleBeanProperties");
    }

    private void processInlineProperties(ConfigurableListableBeanFactory beanFactory, Properties inlineProperties, MutablePropertyValues propertyValues) {
        PropertyValue propertiesPropertyValue = propertyValues.getPropertyValue("properties");
        if (propertiesPropertyValue != null) {
            if (propertiesPropertyValue.getValue() instanceof RuntimeBeanReference) {
                RuntimeBeanReference propertiesRuntimeBeanReference = (RuntimeBeanReference) propertiesPropertyValue
                        .getValue();
                inlineProperties.putAll((Properties) beanFactory.getBean(propertiesRuntimeBeanReference.getBeanName()));
            } else if (propertiesPropertyValue.getValue() instanceof Properties) {
                inlineProperties.putAll((Properties) propertiesPropertyValue.getValue());
            } else if (propertiesPropertyValue.getValue() instanceof TypedStringValue) {
                TypedStringValue value = (TypedStringValue) propertiesPropertyValue.getValue();
                String[] lines = value.getValue().split("\n");
                for (String line : lines) {
                    if (line.trim().length() > 0) {
                        String[] keyValuePair = line.split("=", 2);
                        inlineProperties.put(keyValuePair[0].trim(), keyValuePair[1].trim());
                    }
                }
            } else {
                unknownValueType(propertiesPropertyValue.getValue());
            }
        }
    }

    private void processLocationsProperties(ConfigurableListableBeanFactory beanFactory, Properties locationsProperties, MutablePropertyValues propertyValues) {
        List<String> confResources = retrieveConfigFileList(beanFactory, propertyValues);
        if (confResources.size() > 0) {
            for (String resourcePath : confResources) {
                InputStream inputStream = null;
                try {
                    resourcePath = SystemPropertyUtils.resolvePlaceholders(resourcePath);
                    if (resourcePath.startsWith("file:")) {
                        String fileResourcePath = resourcePath.substring(resourcePath.indexOf(":") + 1);
                        File resourceFile = new File(fileResourcePath);
                        if (resourceFile.exists()) {
                            inputStream = new FileInputStream(resourceFile);
                        }
                    } else {
                        String classpathResourcePath = resourcePath;
                        if (classpathResourcePath.indexOf(":") > 0) {
                            classpathResourcePath = classpathResourcePath.substring(classpathResourcePath.indexOf(":") + 1);
                        }
                        URL resourceUrl = getClass().getResource(classpathResourcePath);
                        if (resourceUrl != null) {
                            inputStream = resourceUrl.openStream();
                        }
                    }
                    if (inputStream == null) {
                        throw new RuntimeException("Cannot find properties file");
                    }
                    locationsProperties.load(inputStream);
                } catch (Exception ex) {
                    throw new RuntimeException("failed to load properties file [" + resourcePath + "]", ex);
                } finally {
                    IOUtils.closeQuietly(inputStream);
                }
            }
        }
    }

    private List<String> retrieveConfigFileList(ConfigurableListableBeanFactory beanFactory, MutablePropertyValues propertyValues) {
        List<String> confResources = new ArrayList<String>();
        PropertyValue locationPropertyValue = propertyValues.getPropertyValue("location");
        if (locationPropertyValue != null) {
            String location = retrieveStringValue(beanFactory, locationPropertyValue.getValue());
            if (location != null) {
                confResources.add(location);
                if (logger.isDebugEnabled())
                    logger.debug("Add original properties location [" + location + "]");
            }
        }
        PropertyValue locationsPropertyValue = propertyValues.getPropertyValue("locations");
        if (locationsPropertyValue != null) {
            if (locationsPropertyValue.getValue() instanceof String[]) {
                String[] values = (String[]) locationsPropertyValue.getValue();
                for (int i = 0; i < values.length; i++) {
                    confResources.add(i, values[i]);
                    if (logger.isDebugEnabled())
                        logger.debug("Add original properties locations [" + values[i] + "]");
                }
            } else if (locationsPropertyValue.getValue() instanceof ManagedList<?>) {
                ManagedList<?> values = (ManagedList<?>) locationsPropertyValue.getValue();
                for (int i = 0; i < values.size(); i++) {
                    String location = retrieveStringValue(beanFactory, values.get(i));
                    confResources.add(i, location);
                    if (logger.isDebugEnabled())
                        logger.debug("Add original properties locations [" + location + "]");
                }
            } else if (locationsPropertyValue.getValue() instanceof TypedStringValue) {
                TypedStringValue value = (TypedStringValue) locationsPropertyValue.getValue();
                String[] values = value.getValue().split(",");
                for (int i = 0; i < values.length; i++) {
                    confResources.add(i, values[i]);
                }
                if (logger.isDebugEnabled())
                    logger.debug("Add original properties locations string [" + value.getValue() + "]");
            } else {
                unknownValueType(locationsPropertyValue);
            }
        }
        return confResources;
    }


    private String retrieveStringValue(ConfigurableListableBeanFactory beanFactory, Object stringValueObject) {
        String stringValue = null;
        if (stringValueObject != null) {
            if (stringValueObject instanceof RuntimeBeanReference) {
                RuntimeBeanReference stringRuntimeBeanReference = (RuntimeBeanReference) stringValueObject;
                stringValue = (String) beanFactory.getBean(stringRuntimeBeanReference.getBeanName());
            } else if (stringValueObject instanceof TypedStringValue) {
                TypedStringValue value = (TypedStringValue) stringValueObject;
                stringValue = value.getValue();
            } else {
                unknownValueType(stringValueObject);
            }
        }
        return stringValue;
    }

    private void unknownValueType(Object propertyValue) {
        throw new RuntimeException("Unknown property value type:" + propertyValue.getClass() + ":" + propertyValue);
    }

}
