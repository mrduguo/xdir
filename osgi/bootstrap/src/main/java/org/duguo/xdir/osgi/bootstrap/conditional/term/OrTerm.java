package org.duguo.xdir.osgi.bootstrap.conditional.term;

import org.duguo.xdir.osgi.spi.conditional.ConditionalTerm;


/**
 * operator for OR
 * 
 * Samples:
 * if__windows__or__linux
 * 
 * @author mrduguo
 *
 */
public class OrTerm implements ConditionalTerm {

	public boolean eval(String... params) {
		return false;
	}

	public int numberOfParams() {
		return -1;
	}

}
