package org.duguo.xdir.infra.spring.config;


import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class EnvironmentPropertiesPlaceholderConfigurerNegativeTest {

    @Test(expectedExceptions = BeanDefinitionStoreException.class)
    public void loadUnresolvablePlaceholderWillThrowException() throws Exception {
        new ClassPathXmlApplicationContext(getClass().getSimpleName() + "-context.xml", getClass());
    }

}
