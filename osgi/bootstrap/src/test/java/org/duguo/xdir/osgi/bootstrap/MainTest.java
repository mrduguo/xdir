package org.duguo.xdir.osgi.bootstrap;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class MainTest {

    private static final String XDIR_OSGI_STOP_PORT = "xdir.osgi.stop.port";
    private int testStopPort=18889;

    @Before
    public void setupEnv(){
        System.setProperty(XDIR_OSGI_STOP_PORT,String.valueOf(testStopPort));
        System.setProperty("xdir.osgi.server.impl.class",MockServer.class.getName());
    }
    @After
    public void resetEnv(){
        System.clearProperty(XDIR_OSGI_STOP_PORT);
    }

    @Test
    public void mainShouldPassWithValidParam() throws Exception{
        final MainMock mainMock=new MainMock();
        try{
            mainMock.execute("status");
        }catch (RuntimeException systemExit){
            assertEquals("System.exit(0)",systemExit.getMessage());
        }
        try{
            mainMock.execute("stop");
        }catch (RuntimeException systemExit){
            assertEquals("System.exit(0)",systemExit.getMessage());
        }

        final Exception[] stopException=new Exception[1];
        Future taskResult= Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    mainMock.execute("start");
                } catch (Exception shutdownException) {
                    stopException[0]=shutdownException;
                }
            }
        });
        Thread.sleep(50);
        try{
            mainMock.execute("stop");
        }catch (RuntimeException systemExit){
            assertEquals("System.exit(-1)",systemExit.getMessage());
        }
        taskResult.get(Long.parseLong(System.getProperty("xdir.osgi.stop.timeout", "100")), TimeUnit.MILLISECONDS);
        assertNotNull(stopException[0]);
        //assertEquals("System.exit(0)", stopException[0].getMessage());
    }

    @Test
    public void mainShouldFailWithInvalidPortNumberOrCommand() throws Exception{
        MainMock mainMock=new MainMock();
        CountDownLatch exitCountDownLatch=new CountDownLatch(3);
        mainMock.exitCountDownLatch=exitCountDownLatch;

        System.setProperty(XDIR_OSGI_STOP_PORT, "abcd");
        try{
            mainMock.execute("status");
        }catch (RuntimeException systemExit){
            assertEquals("System.exit(-1)",systemExit.getMessage());
        }
        assertEquals(2,exitCountDownLatch.getCount());

        System.setProperty(XDIR_OSGI_STOP_PORT, "65536");
        try{
            mainMock.execute("status");
        }catch (RuntimeException systemExit){
            assertEquals("System.exit(-1)",systemExit.getMessage());
        }
        assertEquals(1,exitCountDownLatch.getCount());

        try{
            mainMock.execute("fake-command");
        }catch (RuntimeException systemExit){
            assertEquals("System.exit(-1)",systemExit.getMessage());
        }
        assertEquals(0,exitCountDownLatch.getCount());
    }
}
