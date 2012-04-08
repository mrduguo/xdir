package org.duguo.xdir.infra.spring.context;

import org.duguo.xdir.infra.spring.context.SpringPackageAwareApplicationContext;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class SpringPackageAwareApplicationContextTest {
    @Test
    public void defaultConfigShouldScanConfigurationAndApplyOverride() throws Exception {
        SpringPackageAwareApplicationContext springContextServer=new SpringPackageAwareApplicationContext();
        springContextServer.refresh();
        assertEquals("value from test-context-1_addition.xml", springContextServer.getBean("shouldOverrideByAdditionContext", String.class));
        assertEquals("value from spring-override.xml", springContextServer.getBean("shouldOverrideByOverrideContext", String.class));
        springContextServer.destroy();
    }
    
    @Test
    public void overrideConfigurationLocationsShouldTakePriority() throws Exception {
        System.setProperty(SpringPackageAwareApplicationContext.XDIR_INFRA_SPRING_CONFIGURATION_LOCATIONS,"/spring/test-context-1.xml");
        try{
            SpringPackageAwareApplicationContext springContextServer=new SpringPackageAwareApplicationContext();
            springContextServer.refresh();
            assertEquals("value from test-context-1.xml", springContextServer.getBean("beanFromFirstContext", String.class));
            springContextServer.destroy();
        }finally {
            System.clearProperty(SpringPackageAwareApplicationContext.XDIR_INFRA_SPRING_CONFIGURATION_LOCATIONS);
        }
    }
}
