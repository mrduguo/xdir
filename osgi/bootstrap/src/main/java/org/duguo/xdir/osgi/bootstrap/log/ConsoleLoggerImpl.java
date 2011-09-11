package org.duguo.xdir.osgi.bootstrap.log;


import org.duguo.xdir.osgi.spi.ConsoleLogger;


public class ConsoleLoggerImpl implements  ConsoleLogger
{

	public static final String KEY_XDIR_OSGI_CONSOLE_LOGGER_IMPL = "xdir.osgi.console.logger.impl";
	
	public void log(String... msgs) {
		Logger.log(msgs);
	}

	public void log(Throwable ex, String... msgs) {
		Logger.log(ex, msgs);
	}
	

}
