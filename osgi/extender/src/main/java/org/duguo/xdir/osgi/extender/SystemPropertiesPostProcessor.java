package org.duguo.xdir.osgi.extender;


import org.apache.commons.io.IOUtils;
import org.eclipse.gemini.blueprint.extender.OsgiBeanFactoryPostProcessor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.SystemPropertyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * Override bundle property placeholder configurer to:
 * - use system property as priority
 * - put properties configuration to system properties if not already exist
 *
 * @author Guo Du
 */
public class SystemPropertiesPostProcessor implements OsgiBeanFactoryPostProcessor, PriorityOrdered {
    private static final Logger logger = LoggerFactory.getLogger(SystemPropertiesPostProcessor.class);

    public void postProcessBeanFactory(BundleContext bundleContext, ConfigurableListableBeanFactory beanFactory)
            throws BeansException, InvalidSyntaxException, BundleException {
        String[] placeHolderConfigurers = beanFactory.getBeanNamesForType(PropertyPlaceholderConfigurer.class);
        if (placeHolderConfigurers != null && placeHolderConfigurers.length > 0) {
            if (placeHolderConfigurers.length > 1) {
                logger.warn(placeHolderConfigurers.length
                        + " place holder configurer detected, will only setup first one");
            }
            customisePlaceHolderFactory(bundleContext.getBundle(), beanFactory, placeHolderConfigurers[0]);
            if (logger.isDebugEnabled())
                logger.debug("Configure place holder configurer finished");
        } else {
            customisePlaceHolderFactory(bundleContext.getBundle(), beanFactory, null);
        }
    }


    public int getOrder() {
        // PropertyPlaceholderConfigurer default order is at lowest priority Integer.MAX_VALUE
        return 0;
    }


    private void customisePlaceHolderFactory(Bundle bundle, ConfigurableListableBeanFactory beanFactory,
                                             String placeHolderProcessorName) {
        MutablePropertyValues propertyValues = null;
        if (placeHolderProcessorName != null) {
            BeanDefinition placeHolderBeanDefinition = beanFactory.getBeanDefinition(placeHolderProcessorName);
            propertyValues = placeHolderBeanDefinition.getPropertyValues();
            // system properties will take first priority
            propertyValues.addPropertyValue("systemPropertiesMode", 2);
        }
        appendOverrideProperties(bundle, beanFactory, propertyValues);
    }


    private void appendOverrideProperties(Bundle bundle, ConfigurableListableBeanFactory beanFactory,
                                          MutablePropertyValues propertyValues) {

        Properties bundleSpringProperties = new Properties();
        if (propertyValues != null) {
            processConfiguredProperties(bundleSpringProperties, beanFactory, propertyValues);
            processConfiguredFiles(bundleSpringProperties, beanFactory, propertyValues, bundle);
        }

        List<String> keysNeedResolve = new ArrayList<String>();
        for (Map.Entry entry : bundleSpringProperties.entrySet()) {
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

    private void processConfiguredProperties(Properties bundleSpringProperties, ConfigurableListableBeanFactory beanFactory, MutablePropertyValues propertyValues) {
        PropertyValue propertiesPropertyValue = propertyValues.getPropertyValue("properties");
        if (propertiesPropertyValue != null) {
            if (propertiesPropertyValue.getValue() instanceof RuntimeBeanReference) {
                RuntimeBeanReference propertiesRuntimeBeanReference = (RuntimeBeanReference) propertiesPropertyValue
                        .getValue();
                bundleSpringProperties.putAll((Properties) beanFactory.getBean(propertiesRuntimeBeanReference.getBeanName()));
            } else if (propertiesPropertyValue.getValue() instanceof Properties) {
                bundleSpringProperties.putAll((Properties) propertiesPropertyValue.getValue());
            } else if (propertiesPropertyValue.getValue() instanceof TypedStringValue) {
                TypedStringValue value = (TypedStringValue) propertiesPropertyValue.getValue();
                String[] lines = value.getValue().split("\n");
                for (String line : lines) {
                    if (line.trim().length() > 0) {
                        String[] keyValuePair = line.split("=", 2);
                        bundleSpringProperties.put(keyValuePair[0].trim(), keyValuePair[1].trim());
                    }
                }
            } else {
                unknownValueType(propertiesPropertyValue.getValue());
            }
        }
    }

    private void processConfiguredFiles(Properties bundleSpringProperties, ConfigurableListableBeanFactory beanFactory, MutablePropertyValues propertyValues, Bundle bundle) {
        List<String> confResources = retrieveConfigFileList(beanFactory, propertyValues, bundle);
        if (confResources.size() > 0) {
            for (String resourcePath : confResources) {
                InputStream inputStream = null;
                try {
                    resourcePath = SystemPropertyUtils.resolvePlaceholders(resourcePath);
                    if(resourcePath.startsWith("file:")){
                         String fileResourcePath=resourcePath.substring(resourcePath.indexOf(":")+1);
                        File resourceFile = new File(fileResourcePath);
                        if(!resourceFile.exists()){
                            if (logger.isDebugEnabled())
                                logger.debug("Ignore missing properties file " + fileResourcePath);
                            continue;
                        }
                        inputStream = new FileInputStream(resourceFile);
                    }else {
                        String bundleResourcePath=resourcePath;
                        if(bundleResourcePath.indexOf(":")>0){
                            bundleResourcePath=bundleResourcePath.substring(bundleResourcePath.indexOf(":")+1);
                        }
                        URL resourceUrl = bundle.getResource(bundleResourcePath);
                        if (resourceUrl == null) {
                            if (logger.isDebugEnabled())
                                logger.debug("Ignore missing properties file " + bundleResourcePath);
                            continue;
                        }
                        inputStream = resourceUrl.openStream();
                    }
                    bundleSpringProperties.load(inputStream);
                } catch (Exception ex) {
                    throw new RuntimeException("failed to load properties file [" + resourcePath + "]", ex);
                } finally {
                    IOUtils.closeQuietly(inputStream);
                }
            }
        }
    }

    private List<String> retrieveConfigFileList(ConfigurableListableBeanFactory beanFactory, MutablePropertyValues propertyValues, Bundle bundle) {
        List<String> confResources = new ArrayList<String>();
        PropertyValue locationPropertyValue = propertyValues.getPropertyValue("location");
        if (locationPropertyValue != null) {
            String location = retrieveStringValue(beanFactory, locationPropertyValue.getValue());
            if (location != null) {
                confResources.add(location);
//                propertyValues.removePropertyValue("location");
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
//            propertyValues.removePropertyValue("locations");
        }


        File bundleOverridePropertiesFile = new File(System.getProperty("xdir.home") + "/data/conf/" + bundle.getSymbolicName() + "/spring-override.properties");
        if (bundleOverridePropertiesFile.isFile()) {
            confResources.add("file:"+bundleOverridePropertiesFile.getAbsolutePath());
            if (logger.isDebugEnabled())
                logger.debug("Add override properties file [" + bundleOverridePropertiesFile.getAbsolutePath() + "]");
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
