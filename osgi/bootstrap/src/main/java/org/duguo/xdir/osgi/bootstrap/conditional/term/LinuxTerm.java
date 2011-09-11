package org.duguo.xdir.osgi.bootstrap.conditional.term;
/**
 * pass if the os is Linux by check the property os.name
 * 
 * Samples:
 * if__linux
 * 
 * @author mrduguo
 *
 */
public class LinuxTerm extends EqualsTerm {

	public boolean eval(String... params) {
		return super.eval("os.name","Linux");
	}

	public int numberOfParams() {
		return 0;
	}

}
