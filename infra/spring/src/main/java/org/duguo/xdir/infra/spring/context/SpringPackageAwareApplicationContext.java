package org.duguo.xdir.infra.spring.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SpringPackageAwareApplicationContext extends GenericXmlApplicationContext {

    public static final String XDIR_INFRA_SPRING_CONFIGURATION_LOCATIONS = "xdir.infra.spring.configuration.locations";
    private final static Logger logger = LoggerFactory.getLogger(SpringPackageAwareApplicationContext.class);

    public SpringPackageAwareApplicationContext() {
        super();
        String[] configurationLocations = resolveConfigurationLocations();
        load(configurationLocations);
    }
    public SpringPackageAwareApplicationContext(String[] locations) {
        super();
        load(locations);
    }

    private String[] resolveConfigurationLocations() {
        String userProvidedConfigs = System.getProperty(XDIR_INFRA_SPRING_CONFIGURATION_LOCATIONS);
        String[] configurationLocations;
        if (StringUtils.hasText(userProvidedConfigs)) {
            if (logger.isDebugEnabled())  logger.debug("user provided spring configuration locations [{}]", userProvidedConfigs);
            configurationLocations = userProvidedConfigs.split(",");
        } else {
            configurationLocations = resolveDefaultSpringPackageXmlConfiguration();
        }
        return configurationLocations;
    }

    /**
     * List of spring xml configuration by order:
     * classpath:/spring/*.xml - alphabet order
     * classpath:/spring-override.xml
     *
     * @return classpath spring xml configurations
     */
    public static String[] resolveDefaultSpringPackageXmlConfiguration() {
        String[] configurationLocations;
        List<String> scannedConfigs = new ArrayList<String>();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        scanResources(scannedConfigs, resourcePatternResolver);
        Collections.sort(scannedConfigs);
        scannedConfigs.add("classpath:/spring-override.xml");
        if (logger.isDebugEnabled())
            logger.debug("added default configuration location [{}]", scannedConfigs.get(scannedConfigs.size() - 1));
        configurationLocations = scannedConfigs.toArray(new String[scannedConfigs.size()]);
        return configurationLocations;
    }

    private static void scanResources(List<String> scannedConfigs, ResourcePatternResolver resourcePatternResolver) {
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath*:/spring/*.xml");
            for (Resource resource : resources) {
                String resourcePath = resource.getURL().getPath();
                if (logger.isDebugEnabled()) logger.debug("scanned resource url [{}]", resourcePath);
                resourcePath = "classpath:" + resourcePath.substring(resourcePath.lastIndexOf("/spring/"));
                if (!scannedConfigs.contains(resourcePath)) {
                    if (logger.isDebugEnabled()) logger.debug("added scanned configuration location [{}]", resourcePath);
                    scannedConfigs.add(resourcePath);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to scan configuration in spring package", ex);
        }
    }
}
