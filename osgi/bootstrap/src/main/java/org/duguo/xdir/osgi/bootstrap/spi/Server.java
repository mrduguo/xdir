package org.duguo.xdir.osgi.bootstrap.spi;


public interface Server {
    /**
     * start the server and return once start successfully
     *
     * @throws Exception when failed to start the server
     */
    void start() throws Exception;

    /**
     *
     * stop the server and return once stopped successfully
     *
     * @throws Exception when failed to stop the server
     */
    void stop() throws Exception;
}
