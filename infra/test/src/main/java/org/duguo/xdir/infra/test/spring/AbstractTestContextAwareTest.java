package org.duguo.xdir.infra.test.spring;


import org.duguo.xdir.infra.spring.config.LogConfig;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterSuite;

import java.net.URL;

@ContextConfiguration(loader = SpringPackageAwareApplicationContextLoader.class)
public abstract class AbstractTestContextAwareTest extends AbstractTestNGSpringContextTests{
    static{
        if(System.getProperty("basedir")==null){
            if(!detectBaseDirByFileName("spring-override.xml")){
                detectBaseDirByFileName("spring-override.properties");
            }
        }
        LogConfig.init();
    }

    private static boolean detectBaseDirByFileName(String springOverrideFileName) {
        URL testResourceUrl = AbstractTestContextAwareTest.class.getResource("/" + springOverrideFileName);
        if(testResourceUrl!=null && testResourceUrl.getFile().endsWith("/target/test-classes/" + springOverrideFileName)){
            String basedir=testResourceUrl.getFile();
            basedir=basedir.substring(0,basedir.length()- ("/target/test-classes/" + springOverrideFileName).length());
            System.setProperty("basedir",basedir);
            return true;
        }else{
            return false;
        }
    }

    @AfterSuite
    public void destroySpringContext(){
        ((GenericApplicationContext)applicationContext).destroy();
    }

}
