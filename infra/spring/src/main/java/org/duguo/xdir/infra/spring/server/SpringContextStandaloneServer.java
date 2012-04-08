package org.duguo.xdir.infra.spring.server;

import org.duguo.xdir.infra.spring.context.SpringPackageAwareApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.Lifecycle;


public class SpringContextStandaloneServer implements Lifecycle {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private SpringPackageAwareApplicationContext applicationContext;

    @Override
    public void start() {
        if (logger.isInfoEnabled()) logger.info("Server starting ...");
        try {
            applicationContext = new SpringPackageAwareApplicationContext();
            applicationContext.registerShutdownHook();
            applicationContext.refresh();
            String startupMsg = System.getProperty("xdir.infra.spring.server.started.msg", "Server started");
            logger.info(startupMsg);
            System.out.println(startupMsg);
        } catch (RuntimeException ex) {
            logger.error("Server start failed", ex);
            throw ex;
        }
    }

    @Override
    public void stop() {
        if (logger.isInfoEnabled()) logger.info("Server stopping ...");
        if (applicationContext != null) {
            try {
                applicationContext.destroy();
                applicationContext = null;
            } catch (RuntimeException ex) {
                logger.error("Server stop failed", ex);
                throw ex;
            }
        }
        if (logger.isInfoEnabled()) logger.info("Server stopped");
    }

    @Override
    public boolean isRunning() {
        return applicationContext != null;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
