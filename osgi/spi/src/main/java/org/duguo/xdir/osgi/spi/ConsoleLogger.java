package org.duguo.xdir.osgi.spi;

/**
 * Service interface for user to write log to console. as the default System.our
 * and System.err was redirected to log files.
 * 
 * @author mrduguo
 * 
 */
public interface ConsoleLogger {

	/**
	 * You may specify the msg key and parameters:
	 * "error.xdir.runtime.group.failed","system"
	 * "info.xdir.cmd.status.stopped"
	 * 
	 * Or you can pass the string text instead of key, the implementation could
	 * apply the localised string with the message code as key. For following
	 * example, the message code is FPBTR0009E.
	 * "<FPBTR0009E> Bundle group [{0}] start failed","system"
	 * 
	 * The string text mechanism give user the flexibility to write message to console with localise support.
	 * 
	 * 
	 * @param msgs
	 */
	public void log(String... msgs);

	/**
	 * For log messages with exception. Rest of behaver is same as
	 * log(String...)
	 * 
	 * @param ex
	 * @param msgs
	 */
	public void log(Throwable ex, String... msgs);

}