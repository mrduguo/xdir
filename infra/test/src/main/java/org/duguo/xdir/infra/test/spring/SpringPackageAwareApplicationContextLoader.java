package org.duguo.xdir.infra.test.spring;

import org.duguo.xdir.infra.spring.context.SpringPackageAwareApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextLoader;

public class SpringPackageAwareApplicationContextLoader implements ContextLoader {

    @Override
    public String[] processLocations(Class<?> clazz, String... locations) {
        return locations;
    }

    @Override
    public ApplicationContext loadContext(String... locations) throws Exception {
        SpringPackageAwareApplicationContext springPackageAwareApplicationContext;
        if(locations.length>0){
            springPackageAwareApplicationContext = new SpringPackageAwareApplicationContext(locations);
        } else{
            springPackageAwareApplicationContext = new SpringPackageAwareApplicationContext();
        }
        springPackageAwareApplicationContext.refresh();
        return springPackageAwareApplicationContext;
    }
}