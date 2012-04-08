package org.duguo.xdir.infra.spring.server;


import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.testng.annotations.*;

import static org.testng.Assert.*;

public class MainTest {


    public static int testStopPort=18889;

    @BeforeMethod
    public void setupEnv(){
        System.setProperty(Main.XDIR_INFRA_SPRING_SERVER_STOP_PORT,String.valueOf(testStopPort));
        System.setProperty(Main.XDIR_INFRA_SPRING_SERVER_EMBED_MODE,"true");
    }
    @AfterMethod
    public void resetEnv(){
        System.clearProperty(Main.XDIR_INFRA_SPRING_SERVER_STOP_PORT);
        System.clearProperty(Main.XDIR_INFRA_SPRING_SERVER_EMBED_MODE);
    }

    @Test
    public void mainShouldPassWithValidParam() throws Exception{
        try{
            Main.main("status");
        }catch (Throwable systemExit){
            assertEquals("Main exit with status code 0",systemExit.getMessage());
        }
        try{
            Main.main("stop");
        }catch (Throwable systemExit){
            assertEquals("Main exit with status code 0",systemExit.getMessage());
        }

        final Throwable[] stopException=new Throwable[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Main.main("start");
                } catch (Throwable shutdownException) {
                    stopException[0]=shutdownException;
                }
            }
        }).start();
        Thread.sleep(1000);
        try{
            Main.main("stop");
        }catch (Throwable systemExit){
            assertEquals("Main exit with status code 0", systemExit.getMessage());
        }
        assertNotNull(stopException[0]);
        assertEquals("Main exit with status code 0", stopException[0].getMessage());
    }

    @Test
    public void mainShouldFailWithInvalidPortNumberOrCommand() throws Exception{
        System.setProperty(Main.XDIR_INFRA_SPRING_SERVER_STOP_PORT, "abcd");
        try{
            Main.main("status");
        }catch (Throwable systemExit){
            assertEquals("Main exit with status code -1",systemExit.getMessage());
        }

        System.setProperty(Main.XDIR_INFRA_SPRING_SERVER_STOP_PORT, "65536");
        try{
            Main.main("status");
        }catch (Throwable systemExit){
            assertEquals("Main exit with status code -1",systemExit.getMessage());
        }

        try{
            Main.main("fake-command");
        }catch (Throwable systemExit){
            assertEquals("Main exit with status code -1",systemExit.getMessage());
        }
    }
}
