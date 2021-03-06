package org.duguo.xdir.osgi.bootstrap;


import org.duguo.xdir.osgi.bootstrap.conf.PropertiesUtils;
import org.duguo.xdir.osgi.bootstrap.osgiserver.DefaultOsgiServer;
import org.duguo.xdir.osgi.bootstrap.spi.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The Main class to boot the OSGi server
 */
public class Main {

    private static final String SUPPORTED_COMMANDS = "start|stop|status";


    public static void main(String... args) throws Exception {
        new Main().execute(args);
    }

    protected void execute(String... args) throws Exception{
        String command = parseCommand(args);
        int stopPort = parseStopPort();
        ServerSocket serverSocket=null;
        try {
            serverSocket = new ServerSocket(stopPort, 1, InetAddress.getByName("127.0.0.1"));
            withStoppedServer(command, serverSocket);
        } catch (IOException ex) {
            withRunningServer(command,stopPort);
        }finally {
            if(serverSocket!=null)
                serverSocket.close();
        }

    }

    private void withStoppedServer(String command, ServerSocket serverSocket) throws Exception {
        if (command.equals("start")) {
            startServer(serverSocket);
        } else if (command.equals("stop")) {
            System.out.println("Server was already stopped");
            exit(0);
        } else if (command.equals("status")) {
            System.out.println("Server is NOT running");
            exit(0);
        }
    }

    private void withRunningServer(String command, int stopPort) throws Exception{
        if (command.equals("stop")) {
            Socket clientSocket = new Socket("127.0.0.1", stopPort);
            try {
                clientSocket.getInputStream().read();
                System.out.println("Server stopped successfully");
                Thread.sleep(100); // give little time for server to come down
                exit(0);
            } catch (Exception ignore) {
                ignore.printStackTrace();
                exit(-1,"Server stopped unexpected: "+ignore.getMessage());
            }
        } else if (command.equals("start")) {
            exit(-1,"Server was already running");
        } else{
            System.out.println("Server is running");
        }
    }

    private void startServer(ServerSocket serverSocket) throws Exception {
        PropertiesUtils.loadOsgiProperties();
        final Server server = (Server)Class.forName(System.getProperty("xdir.osgi.server.impl.class",DefaultOsgiServer.class.getName())).newInstance();
        Thread shutdownHook = addShutdownHook(server);
        startServerInBootstrap(server);
        waitForStopRequest(server, serverSocket,shutdownHook);
    }

    private void startServerInBootstrap(final Server server) {
        Thread bootstrap=new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    System.out.println("Server starting ...");
                    server.start();
                }catch (Exception ex){
                    PropertiesUtils.logSystemProperties();
                    ex.printStackTrace();
                    exit(-1, "Server start failed");
                }
                PropertiesUtils.logSystemProperties();
            }
        });
        bootstrap.setName("infra");
        bootstrap.start();
    }

    private Thread addShutdownHook(final Server server) {
        Thread shutdownHook=new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    server.stop();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        shutdownHook.setName("shutdown-hook");
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        return shutdownHook;
    }

    private void waitForStopRequest(final Server server,ServerSocket serverSocket,Thread shutdownHook) throws Exception {
        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
            System.out.println("Received stop request and shutting down server ...");
            Runtime.getRuntime().removeShutdownHook(shutdownHook);
           stopServer(server, clientSocket);
        } finally {
            System.out.println("Server stopped");
            closeClientStopRequestAndExit(clientSocket,0,null);
        }
    }

    private void stopServer(Server server, Socket clientSocket) throws Exception {
        try{
            server.stop();
        }catch (Exception ex){
            ex.printStackTrace();
            closeClientStopRequestAndExit(clientSocket, -1, "Server stop failed, will force exit");
        }
    }

    private void closeClientStopRequestAndExit(Socket clientSocket,int exitStatus,String msg) throws IOException {
        try {
            if (clientSocket != null) {
                clientSocket.getOutputStream().write(0);
                clientSocket.close();
            }
        } finally {
            exit(exitStatus,msg);
        }
    }

    protected void exit(int statusCode,String msg) {
        if(msg!=null)
            System.err.println(msg);
        exit(statusCode);
    }

    protected void exit(int statusCode) {
        System.exit(statusCode);
    }

    private int parseStopPort() {
        String stopPortString=System.getProperty("xdir.osgi.stop.port");
        try{
            if(stopPortString!=null){
                int stopPort=Integer.parseInt(stopPortString);
                if(stopPort<1024 || stopPort>65535){
                    exit(-1,"stop port "+stopPortString+" is out of supported range [1024-65535]");
                }
                return stopPort;
            }
        }catch (NumberFormatException ex){
            exit(-1,"stop port "+stopPortString+" is a invalid number");
        }
        return 18888;
    }

    private String parseCommand(String[] args) {
        String command;
        if (args.length > 0) {
            command = args[0];
            if(!SUPPORTED_COMMANDS.contains(command)){
                exit(-1,command+" is not a supported ["+SUPPORTED_COMMANDS+"] command");
            }
            if(args.length>1 && args[1].equals("-debug")){
                System.setProperty("debug","true");
            }
        } else {
            command = "start";
        }
        return command;
    }


}