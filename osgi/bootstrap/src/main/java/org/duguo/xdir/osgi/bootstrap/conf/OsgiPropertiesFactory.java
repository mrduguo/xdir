package org.duguo.xdir.osgi.bootstrap.conf;


import org.apache.log4j.xml.DOMConfigurator;
import org.duguo.xdir.osgi.bootstrap.command.FileUtils;
import org.duguo.xdir.osgi.bootstrap.i18n.Messages;
import org.duguo.xdir.osgi.bootstrap.i18n.MessagesInitialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;


/**
 * Factory to load OSGi configuration file from XDIR_DIR_HOME/osgi.properties by default.
 *
 * @author Guo Du
 */
public class OsgiPropertiesFactory {
    private static final Logger logger = LoggerFactory.getLogger(OsgiPropertiesFactory.class);

    public OsgiProperties createOsgiProperties() {
        OsgiProperties configuration = new OsgiProperties();
        verifyXdirDirHome(configuration);

        loadConfigFile(configuration);
        MessagesInitialiser.init(Messages.class, configuration.getXdirDirHome() + "/data/conf/osgi.messages");


        configLog4j();

        return configuration;
    }

    private void verifyXdirDirHome(OsgiProperties configuration) {
        String xdirHome = configuration.getXdirDirHome();
        if (xdirHome == null) {
            try {
                File bootstrapJarFile = FileUtils.retriveJarFileContainsClass(this.getClass());
                xdirHome = bootstrapJarFile.getParentFile().getParentFile().getCanonicalPath();
                configuration.setXdirDirHome(xdirHome);
                if (logger.isDebugEnabled())
                    logger.debug("Detected XDIR_DIR_HOME [" + xdirHome + "] from jar file [" + bootstrapJarFile + "]");
            } catch (Exception e) {
                throw new RuntimeException(Messages.ERROR_XDIR_FILE_FAIL_RESOLVE_XDIRHOME, e);
            }
        } else {
            if (logger.isDebugEnabled())
                logger.debug("Retrived XDIR_DIR_HOME [" + xdirHome + "] from configuration");
        }
        FileUtils.verifyExistFolder(xdirHome);
        configuration.retriveXdirDirData();
    }


    private void loadConfigFile(OsgiProperties configuration) {
        try {
            Properties newProperties = new Properties();
            InputStream defaultConfig = OsgiProperties.class.getResourceAsStream("/osgi-default.properties");
            newProperties.load(defaultConfig);
            defaultConfig.close();

            InputStream userConfig = OsgiProperties.class.getResourceAsStream("/osgi.properties");
            if (userConfig != null) {
                newProperties.load(userConfig);
                userConfig.close();
            }

            PropertiesUtils.replacePlaceHolders(configuration, newProperties);
            if (logger.isDebugEnabled())
                logger.debug("OSGi configuration loaded");
        } catch (Exception e) {
            throw new RuntimeException(Messages.ERROR_XDIR_CONF_LOAD_FILE_FAILED, e);
        }
    }


    private static void configLog4j() {
        String log4jConfiguration = System.getProperty("log4j.configuration", "/log4j.xml");
        URL log4jResourceUrl = OsgiPropertiesFactory.class.getResource(log4jConfiguration);
        if (log4jResourceUrl != null) {
            if (log4jResourceUrl.getProtocol().equals("file")) {
                String delayConfig = System.getProperty("xdir.osgi.log.config.scan.delay","10000");
                DOMConfigurator.configureAndWatch(log4jConfiguration, Long.parseLong(delayConfig));
            }
        }
    }



}