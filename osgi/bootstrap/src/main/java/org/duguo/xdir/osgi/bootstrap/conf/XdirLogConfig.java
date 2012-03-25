package org.duguo.xdir.osgi.bootstrap.conf;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.util.logging.LogManager;

public class XdirLogConfig {

    public static final PrintStream rawStdOut = System.out;
    public static final PrintStream rawStdErr = System.err;
    public static final String LOG4J_LOGGER_CONFIG_PREFIX = "log4j.logger.";

    public static void configLog(){
        String log4jConfiguration = System.getProperty("log4j.configuration");
        if (log4jConfiguration == null) {
            smartLogConfig();
        }
    }

    private static void smartLogConfig(){
        String log4jConfiguration;
        String debugFlag=System.getProperty("debug");

        if (debugFlag!=null && (debugFlag.length()==0 || "true".equals(debugFlag))){
            if(debugFlag.length()==0){
                System.setProperty("debug", "true");
            }
            log4jConfiguration = "/log4j-debug.xml";
        }else
            log4jConfiguration = "/log4j.xml";

        File logFile=new File(System.getProperty("xdir.log.file"));
        if(!logFile.getParentFile().exists()){
            logFile.getParentFile().mkdirs();
        }

        URL log4jResourceUrl = XdirLogConfig.class.getResource(log4jConfiguration);
        if (log4jResourceUrl.getProtocol().equals("file")) {
            String delayConfig = System.getProperty("xdir.osgi.log.config.scan.delay", "10000");
            DOMConfigurator.configureAndWatch(log4jResourceUrl.getFile(), Long.parseLong(delayConfig));
        }else
            DOMConfigurator.configure(log4jResourceUrl);

        Logger logger = Logger.getLogger(XdirLogConfig.class.getPackage().getName()+".ConsoleLog");
        if (logger.isDebugEnabled()) {
            logger.debug("setup smart debug with log config: " + log4jResourceUrl.toString());
            setupDebugLog( logger);
        }else{
            applyLog4jLoggerConfigViaSystemProperty();
        }
    }

    /**
     * You may config package log level via system properties e.g.:
     *
     * log4j.logger.org.apache.axiom=WARN
     *
     */
    private static void applyLog4jLoggerConfigViaSystemProperty() {
        for(String key:System.getProperties().stringPropertyNames()){
           if(key.startsWith(LOG4J_LOGGER_CONFIG_PREFIX)){
               String loggerName=key.substring(LOG4J_LOGGER_CONFIG_PREFIX.length());
               String logLevel=System.getProperty(key);
               Logger.getLogger(loggerName).setLevel(Level.toLevel(logLevel));
           }
        }
    }

    public static void setupDebugLog(Logger logger) {
        initJul();
        redirectSystemOutAndErrToLogWhenDebug( logger);
    }

    private static void initJul() {
        LogManager.getLogManager().getLogger("").setLevel(java.util.logging.Level.FINER);
        SLF4JBridgeHandler.install();
    }

    public static void redirectSystemOutAndErrToLogWhenDebug(Logger logger) {
        if ("true".equals(System.getProperty("xdir.osgi.redirect.std.out.err.to.log", "true"))) {
            System.setOut(createLoggingProxy(System.out, Level.INFO, logger));
            System.setErr(createLoggingProxy(System.err, Level.ERROR, logger));
        }
    }

    private static PrintStream createLoggingProxy(final PrintStream realPrintStream, final Level level,final Logger logger) {
        return new PrintStream(realPrintStream) {
            public void print(final String string) {
                realPrintStream.print(string);
                logger.log(level, string);
            }
        };
    }
}


