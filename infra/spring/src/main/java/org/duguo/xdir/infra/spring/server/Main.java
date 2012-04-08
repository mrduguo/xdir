package org.duguo.xdir.infra.spring.server;


import org.duguo.xdir.infra.spring.config.LogConfig;
import org.springframework.context.Lifecycle;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The Main class to boot up the server
 */
public class Main {

    public static final String XDIR_INFRA_SPRING_SERVER_SUPPORTED_COMMANDS = "start|stop|status";
    public static final String XDIR_INFRA_SPRING_SERVER_EMBED_MODE = "xdir.infra.spring.server.embed.mode";
    public static final String XDIR_INFRA_SPRING_SERVER_IMPL_CLASS = "xdir.infra.spring.server.impl.class";
    public static final String XDIR_INFRA_SPRING_SERVER_IMPL_CLASS_DEFAULT = "org.duguo.xdir.infra.spring.server.SpringContextStandaloneServer";
    public static final String XDIR_INFRA_SPRING_SERVER_STOP_PORT = "xdir.infra.spring.server.stop.port";


    public static void main(String... args) throws Exception {
        new Main().execute(args);
    }

    private void execute(String... args) throws Exception {
        String command = parseCommand(args);
        int stopPort = parseStopPort();
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(stopPort, 1, InetAddress.getByName("127.0.0.1"));
            withStoppedServer(command, serverSocket);
        } catch (IOException ex) {
            withRunningServer(command, stopPort);
        } finally {
            if (serverSocket != null)
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

    private void withRunningServer(String command, int stopPort) throws Exception {
        if (command.equals("stop")) {
            Socket clientSocket = new Socket("127.0.0.1", stopPort);
            try {
                clientSocket.getInputStream().read();
                System.out.println("Server stopped successfully");
                Thread.sleep(100); // give little time for server to come down
                exit(0);
            } catch (Exception ignore) {
                ignore.printStackTrace();
                exit(-1, "Server stopped unexpected: " + ignore.getMessage());
            }
        } else if (command.equals("start")) {
            exit(-1, "Server was already running");
        } else {
            System.out.println("Server is running");
        }
    }

    private void startServer(ServerSocket serverSocket) throws Exception {
        LogConfig.init();
        final Lifecycle server = (Lifecycle) Class.forName(System.getProperty(XDIR_INFRA_SPRING_SERVER_IMPL_CLASS, XDIR_INFRA_SPRING_SERVER_IMPL_CLASS_DEFAULT)).newInstance();
        startServerInBootstrap(server);
        waitForStopRequest(server, serverSocket);
    }

    private void startServerInBootstrap(final Lifecycle server) {
        Thread bootstrap = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Server starting ...");
                    server.start();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    exit(-1, "Server start failed");
                }
            }
        });
        bootstrap.setName("bootstrap");
        bootstrap.start();
    }

    private void waitForStopRequest(final Lifecycle server, ServerSocket serverSocket) throws Exception {
        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
            System.out.println("Received stop request and shutting down server ...");
            stopServer(server, clientSocket);
        } finally {
            System.out.println("Server stopped");
            closeClientStopRequestAndExit(clientSocket, 0, null);
        }
    }

    private void stopServer(Lifecycle server, Socket clientSocket) throws Exception {
        try {
            server.stop();
        } catch (Exception ex) {
            ex.printStackTrace();
            closeClientStopRequestAndExit(clientSocket, -1, "Server stop failed, will force exit");
        }
    }

    private void closeClientStopRequestAndExit(Socket clientSocket, int exitStatus, String msg) throws IOException {
        try {
            if (clientSocket != null) {
                clientSocket.getOutputStream().write(0);
                clientSocket.close();
            }
        } finally {
            exit(exitStatus, msg);
        }
    }

    protected void exit(int statusCode, String msg) {
        if (msg != null)
            System.err.println(msg);
        exit(statusCode);
    }

    protected void exit(int statusCode) {
        if ("false".equals(System.getProperty(XDIR_INFRA_SPRING_SERVER_EMBED_MODE, "false")))
            System.exit(statusCode);
        else
            throw new Error("Main exit with status code " + statusCode);
    }

    private int parseStopPort() {
        String stopPortString = System.getProperty(XDIR_INFRA_SPRING_SERVER_STOP_PORT);
        try {
            if (stopPortString != null) {
                int stopPort = Integer.parseInt(stopPortString);
                if (stopPort < 1024 || stopPort > 65535) {
                    exit(-1, "stop port " + stopPortString + " is out of supported range [1024-65535]");
                }
                return stopPort;
            }
        } catch (NumberFormatException ex) {
            exit(-1, "stop port " + stopPortString + " is a invalid number");
        }
        return 18888;
    }

    private String parseCommand(String[] args) {
        String command;
        if (args.length > 0) {
            command = args[0];
            if (!XDIR_INFRA_SPRING_SERVER_SUPPORTED_COMMANDS.contains(command)) {
                exit(-1, command + " is not a supported [" + XDIR_INFRA_SPRING_SERVER_SUPPORTED_COMMANDS + "] command");
            }
        } else {
            command = "start";
        }
        return command;
    }


}