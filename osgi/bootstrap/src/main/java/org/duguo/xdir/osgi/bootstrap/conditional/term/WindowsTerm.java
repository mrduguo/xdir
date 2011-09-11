package org.duguo.xdir.osgi.bootstrap.conditional.term;

/**
 * pass if the os is Windows by check the property os.name
 * 
 * Samples:
 * if__windows
 * 
 * @author mrduguo
 *
 */
public class WindowsTerm extends EqualsTerm {

	public boolean eval(String... params) {
		return super.eval("os.name","Windows");
	}

	public int numberOfParams() {
		return 0;
	}

}
