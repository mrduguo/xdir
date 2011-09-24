package org.duguo.xdir.osgi.extender.conf;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.core.PriorityOrdered;
import org.eclipse.gemini.blueprint.extender.OsgiBeanFactoryPostProcessor;


/**
 * Properties place holder configurer . Supported sample configurations:
 * 
    <osgix:cm-properties id="cmProps" persistent-id="com.xyz.myapp">
        <prop key="key">value</prop>
    </osgix:cm-properties>
    <ctx:property-placeholder properties-ref="cmProps"/>    
    
    <ctx:property-placeholder location="classpath:foo.properties"/>
    
    <ctx:property-placeholder location="classpath:foo.properties,classpath:bar.properties"/>
    
    <bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
        <property name="properties">
          <props>
            <prop key="key">value</prop>
          </props>
        </property>
        <property name="locations">
          <list>
            <value>classpath:foo.properties</value>
            <value>classpath:bar.properties</value>
          </list>
        </property>
    </bean>
    
    <bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
        <property name="properties">
            <value>
                key=value
                foo=bar
            </value>
        </property>
        <property name="locations" value="classpath:foo.properties,classpath:bar.properties"/>
    </bean>
    
 * 
 * 
 * By default, the configurer will look for spring*.properties at following location and append to locations property.
 * The bundle.version and bundle.symbolic.name will append to properties property.
 * 
 * XDIR_DIR_CONF
 * XDIR_DIR_CONF/BUNDLE_SYMBOLIC_NAME
 * XDIR_DIR_CONF/BUNDLE_SYMBOLIC_NAME/BUNDLE_VERSION
 * 
 * @author Guo Du
 *
 */
public class ConfFolderAwarePropertiesPostProcessor implements OsgiBeanFactoryPostProcessor, PriorityOrdered
{
    private static final Logger logger = LoggerFactory.getLogger( ConfFolderAwarePropertiesPostProcessor.class );


    public void postProcessBeanFactory( BundleContext bundleContext, ConfigurableListableBeanFactory beanFactory )
        throws BeansException, InvalidSyntaxException, BundleException
    {
        String[] placeHolderConfigurers = beanFactory.getBeanNamesForType( PropertyPlaceholderConfigurer.class );
        if ( placeHolderConfigurers != null && placeHolderConfigurers.length > 0 )
        {
            if ( placeHolderConfigurers.length > 1 )
            {
                logger.warn( placeHolderConfigurers.length
                    + " place holder configurer detected, will only setup first one" );
            }
            customisePlaceHolderFactory( bundleContext.getBundle(), beanFactory, placeHolderConfigurers[0] );
            if ( logger.isDebugEnabled() )
                logger.debug( "Configure place holder configurer finished" );
        }
    }


    public int getOrder()
    {
        // PropertyPlaceholderConfigurer default order is at lowest priority Integer.MAX_VALUE
        return 0;
    }


    private void customisePlaceHolderFactory( Bundle bundle, ConfigurableListableBeanFactory beanFactory,
        String placeHolderProcessorName )
    {
        BeanDefinition placeHolderBeanDefinition = beanFactory.getBeanDefinition( placeHolderProcessorName );
        MutablePropertyValues propertyValues = placeHolderBeanDefinition.getPropertyValues();

        // system properties will take first priority
        propertyValues.addPropertyValue( "systemPropertiesMode", 2 );

        appendBundleNameAndVersion( bundle, beanFactory, propertyValues );

        appendLocalProperties( bundle, beanFactory, propertyValues );
    }


    private void appendLocalProperties( Bundle bundle, ConfigurableListableBeanFactory beanFactory,
        MutablePropertyValues propertyValues )
    {
        List<String> confFiles = new ArrayList<String>();
        PropertyValue locationPropertyValue = propertyValues.getPropertyValue( "location" );
        if ( locationPropertyValue != null )
        {
            String location = retriveStringValue( beanFactory, locationPropertyValue.getValue() );
            if ( location != null )
            {
                confFiles.add( location );
                propertyValues.removePropertyValue( "location" );
                if ( logger.isDebugEnabled() )
                    logger.debug( "Add original properties location [" + location + "]" );
            }
        }
        else
        {
            PropertyValue locationsPropertyValue = propertyValues.getPropertyValue( "locations" );
            if ( locationsPropertyValue != null )
            {
                if ( locationsPropertyValue.getValue() instanceof String[] )
                {
                    String[] values = ( String[] ) locationsPropertyValue.getValue();
                    for ( int i = 0; i < values.length; i++ )
                    {
                        confFiles.add( i, values[i] );
                        if ( logger.isDebugEnabled() )
                            logger.debug( "Add original properties locations [" + values[i] + "]" );
                    }
                }
                else if ( locationsPropertyValue.getValue() instanceof ManagedList<?> )
                {
                    ManagedList<?> values = ( ManagedList<?> ) locationsPropertyValue.getValue();
                    for ( int i = 0; i < values.size(); i++ )
                    {
                        String location = retriveStringValue( beanFactory, values.get( i ) );
                        confFiles.add( i, location );
                        if ( logger.isDebugEnabled() )
                            logger.debug( "Add original properties locations [" + location + "]" );
                    }
                }
                else if ( locationsPropertyValue.getValue() instanceof TypedStringValue )
                {
                    TypedStringValue value = ( TypedStringValue ) locationsPropertyValue.getValue();
                    String[] values = value.getValue().split( "," );
                    for ( int i = 0; i < values.length; i++ )
                    {
                        confFiles.add( i, values[i] );
                    }
                    if ( logger.isDebugEnabled() )
                        logger.debug( "Add original properties locations string [" + value.getValue() + "]" );
                }
                else
                {
                    unknownValueType( locationsPropertyValue );
                }
            }
        }
        ConfFolderAwareScannerUtils.scanLocalConfigurations( bundle, confFiles,
            ConfFolderAwareScannerUtils.VALUE_XDIR_EXTENDER_CONF_PROP );
        if ( confFiles.size() > 0 )
        {
            String[] locations = new String[confFiles.size()];
            for ( int i = 0; i < locations.length; i++ )
            {
                locations[i] = confFiles.get( i );
                if ( logger.isDebugEnabled() )
                    logger.debug( "Final properties locations [" + locations[i] + "]" );
            }
            propertyValues.addPropertyValue( "locations", locations );
        }
    }


    private void appendBundleNameAndVersion( Bundle bundle, ConfigurableListableBeanFactory beanFactory,
        MutablePropertyValues propertyValues )
    {
        Properties properties = null;
        PropertyValue propertiesPropertyValue = propertyValues.getPropertyValue( "properties" );
        if ( propertiesPropertyValue == null )
        {
            properties = new Properties();
            propertyValues.addPropertyValue( "properties", properties );
        }
        else if ( propertiesPropertyValue.getValue() instanceof RuntimeBeanReference )
        {
            RuntimeBeanReference propertiesRuntimeBeanReference = ( RuntimeBeanReference ) propertiesPropertyValue
                .getValue();
            properties = ( Properties ) beanFactory.getBean( propertiesRuntimeBeanReference.getBeanName() );
        }
        else if ( propertiesPropertyValue.getValue() instanceof Properties )
        {
            properties = ( Properties ) propertiesPropertyValue.getValue();
        }
        else if ( propertiesPropertyValue.getValue() instanceof TypedStringValue )
        {
            TypedStringValue value = ( TypedStringValue ) propertiesPropertyValue.getValue();
            StringBuilder newPropsString = new StringBuilder();
            newPropsString.append( value.getValue() );
            newPropsString.append( "\nbundle.version=" + bundle.getVersion().toString() );
            newPropsString.append( "\nbundle.symbolic.name=" + bundle.getSymbolicName() );
            value.setValue( newPropsString.toString() );
            return;
        }
        else
        {
            unknownValueType( propertiesPropertyValue.getValue() );
        }
        properties.put( "bundle.version", bundle.getVersion().toString() );
        properties.put( "bundle.symbolic.name", bundle.getSymbolicName() );
    }


    private String retriveStringValue( ConfigurableListableBeanFactory beanFactory, Object stringValueObject )
    {
        String stringValue = null;
        if ( stringValueObject != null )
        {
            if ( stringValueObject instanceof RuntimeBeanReference )
            {
                RuntimeBeanReference stringRuntimeBeanReference = ( RuntimeBeanReference ) stringValueObject;
                stringValue = ( String ) beanFactory.getBean( stringRuntimeBeanReference.getBeanName() );
            }
            else if ( stringValueObject instanceof TypedStringValue )
            {
                TypedStringValue value = ( TypedStringValue ) stringValueObject;
                stringValue = value.getValue();
            }
            else
            {
                unknownValueType( stringValueObject );
            }
        }
        return stringValue;
    }


    private void unknownValueType( Object propertyValue )
    {
        throw new RuntimeException( "Unknown property value type:" + propertyValue.getClass() + ":" + propertyValue );
    }

}
