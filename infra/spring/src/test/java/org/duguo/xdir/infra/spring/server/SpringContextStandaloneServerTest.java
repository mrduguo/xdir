package org.duguo.xdir.infra.spring.server;

import org.duguo.xdir.infra.spring.config.LogConfig;

import org.testng.annotations.Test;
import static org.testng.Assert.*;


public class SpringContextStandaloneServerTest {
    @Test
    public void testIsRunning() throws Exception {
        LogConfig.init();
        SpringContextStandaloneServer server=new SpringContextStandaloneServer();
        assertFalse(server.isRunning());
        server.start();
        assertTrue(server.isRunning());
        server.stop();
        assertFalse(server.isRunning());
    }
}
