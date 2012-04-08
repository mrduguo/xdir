package org.duguo.xdir.infra.spring.config;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.RootLogger;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.LogManager;

public class LogConfig {

    public static final String XDIR_INFRA_SPRING_SERVER_LOG_CONFIG_SCAN_DELAY = "xdir.infra.spring.server.log.config.scan.delay";

    public static void init() {
        ensureDefaultLogFolderExist();
        setupLog4jConfigAndWatch();
        redirectJulToLog4jWhenDebug();
    }

    private static void ensureDefaultLogFolderExist() {
        String xdirHome = System.getProperty("xdir.home");
        if(xdirHome ==null){
            String basedir=System.getProperty("basedir");
            xdirHome = (basedir != null ? basedir + "/" : "") + "target/xdir-test-home";
            xdirHome=new File(xdirHome).getAbsolutePath();
            System.setProperty("xdir.home", xdirHome);
            System.out.println("set xdir.home="+xdirHome);
        }
        File logFolder = new File(System.getProperty("xdir.home"), "var/log");
        if (!logFolder.exists()) {
            logFolder.mkdirs();
        }
    }

    private static void setupLog4jConfigAndWatch() {
        String log4jConfiguration = System.getProperty("log4j.configuration");
        if (log4jConfiguration == null)
            log4jConfiguration = "/log4j.xml";

        URL log4jResourceUrl = LogConfig.class.getResource(log4jConfiguration);
        if(log4jResourceUrl!=null){
            if (log4jResourceUrl.getProtocol().equals("file")) {
                String delayConfig = System.getProperty(XDIR_INFRA_SPRING_SERVER_LOG_CONFIG_SCAN_DELAY, "10000");
                DOMConfigurator.configureAndWatch(log4jResourceUrl.getFile(), Long.parseLong(delayConfig));
            } else
                DOMConfigurator.configure(log4jResourceUrl);
        }


        applyCommandLineLogLevel();

        Logger logger = Logger.getLogger(LogConfig.class);
        if (logger.isDebugEnabled()) {
            logger.debug("setup log4j config with url [" + log4jResourceUrl + "]");
        }
    }

    private static void applyCommandLineLogLevel() {
        if(System.getProperty("log")!=null){
            String levelString=System.getProperty("log");
            if("ignore".equals(levelString))
                return;
            if("true".equals(levelString))
                levelString="DEBUG";
            setAllLoggersLevel(Level.toLevel(levelString));
        }else{
            boolean isJdwpDebug=false;
            for(String arg: ManagementFactory.getRuntimeMXBean().getInputArguments()){
                if(arg.contains("jdwp")){
                    isJdwpDebug=true;
                }
            }
            if(isJdwpDebug){
                setAllLoggersLevel(Level.DEBUG);
            }
        }
    }

    private static void setAllLoggersLevel(Level level) {
        setLoggerLevel(RootLogger.getRootLogger(), level);
        Enumeration currentLoggers = RootLogger.getRootLogger().getLoggerRepository().getCurrentLoggers();
        while(currentLoggers.hasMoreElements()){
            Logger logger = (Logger) currentLoggers.nextElement();
            setLoggerLevel(logger, level);
        }
    }

    private static void setLoggerLevel(Logger logger, Level level) {
        logger.setLevel(level);
        RootLogger.getRootLogger().debug("set logger level " + logger.getName() + "=" + level);
    }

    private static void redirectJulToLog4jWhenDebug() {
        Logger logger = Logger.getLogger(LogConfig.class);
        if (logger.isDebugEnabled()) {
            LogManager.getLogManager().getLogger("").setLevel(java.util.logging.Level.FINER);
            SLF4JBridgeHandler.install();
            logger.debug("redirected jul to log4j");
        }
    }
}


