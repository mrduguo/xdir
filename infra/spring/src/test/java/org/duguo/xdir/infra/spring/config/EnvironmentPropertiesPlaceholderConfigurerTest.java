package org.duguo.xdir.infra.spring.config;

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration
public class EnvironmentPropertiesPlaceholderConfigurerTest extends AbstractTestNGSpringContextTests {


    @Test
    public void propertiesPlaceholderShouldBeResolved() throws Exception {
         assertEquals("value from system property "+System.getProperty("user.name"),applicationContext.getBean("shouldResolvePlaceHolderFromSystemProperty"));
         assertEquals("value from environment property "+System.getenv("USER"),applicationContext.getBean("shouldResolvePlaceHolderFromEnvironmentProperty"));
         assertEquals("value from spring-override.properties",applicationContext.getBean("shouldResolvePlaceHolderFromPropertiesFile"));
         assertEquals("value from spring-override.properties",applicationContext.getBean("shouldResolvePlaceHolderFromPropertiesFile"));
         assertEquals("value from spring-override.properties",System.getProperty("test.property.from.spring.override"));
         assertEquals("value from spring-override.properties and value with user name: "+System.getProperty("user.name"),System.getProperty("test.property.contain.placeholder"));
         assertEquals("value from inline property with place holder key with user name: "+System.getProperty("user.name"),System.getProperty("test.property.inline.key"));
    }
}
