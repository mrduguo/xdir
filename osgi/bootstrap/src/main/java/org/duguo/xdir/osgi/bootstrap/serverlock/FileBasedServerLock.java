package org.duguo.xdir.osgi.bootstrap.serverlock;

import org.duguo.xdir.osgi.spi.bootstrap.ServerLock;

/**
 * 
 * @author mrduguo
 *
 */
public class FileBasedServerLock implements ServerLock {

	public boolean isServerRunning() {
		return false;
	}

}
