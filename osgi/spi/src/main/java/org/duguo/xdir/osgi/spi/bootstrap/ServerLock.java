package org.duguo.xdir.osgi.spi.bootstrap;

/**
 * A lock to tell whether the server is running or not. This is used when command need to know the server status.
 * 
 * @author mrduguo
 *
 */
public interface ServerLock {
	
	/**
	 * @return true if server is in running status
	 */
	boolean isServerRunning();
	
}