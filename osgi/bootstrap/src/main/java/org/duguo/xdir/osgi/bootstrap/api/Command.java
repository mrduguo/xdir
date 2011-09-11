package org.duguo.xdir.osgi.bootstrap.api;

import org.duguo.xdir.osgi.bootstrap.conf.OsgiProperties;


public interface Command
{
    
    /**
     * restart exit code, used by startup script
     */
    public static final int RESTART_EXIT_CODE = 5;

    /**
     * 
     * @param configuration     Configuration loaded from configuration file
     * @return  command status which will return back to the startup script
     * @throws Exception
     */
    int execute(OsgiProperties configuration) throws Exception;
    
    /**
     * 
     * @return  Configuration passed by execute method
     */
    OsgiProperties getConfiguration();

    /**
     * 
     * @return  true if server is running
     */
    boolean isServerRunning( );
    
    /**
     * 
     * @return  milliseconds of command thread wait period
     */
    long getCommandPollInterval();
    
    /**
     * 
     * @return  command timeout count, = timeoutMilliseconds/pollInterval
     */
    long getCommandTimeoutCount();
    
}
