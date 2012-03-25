package org.duguo.xdir.osgi.bootstrap;

import org.duguo.xdir.osgi.bootstrap.spi.Server;

import java.util.concurrent.Callable;


public class MockServer implements Server {
    public static Callable actionToPerform;
    public void start() throws Exception{
        if (actionToPerform != null)
            actionToPerform.call();
        Thread.sleep(100);
    }
    public void stop() throws Exception{
        if (actionToPerform != null)
            actionToPerform.call();
        Thread.sleep(10);
    }
}