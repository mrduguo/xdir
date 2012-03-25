package org.duguo.xdir.osgi.bootstrap;


import java.util.concurrent.CountDownLatch;

/**
 * The Main class to boot the OSGi server
 */
public class MainMock extends Main{

    public CountDownLatch exitCountDownLatch;

    protected void exit(int statusCode) {
        if(exitCountDownLatch!=null)
            exitCountDownLatch.countDown();
        throw new RuntimeException("System.exit("+statusCode+")");
    }


}