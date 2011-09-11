package org.duguo.xdir.osgi.bootstrap.conditional.term;

import org.duguo.xdir.osgi.spi.conditional.ConditionalTerm;


/**
 * operator for NOT
 * 
 * Samples:
 * if__not__windows
 * 
 * @author mrduguo
 *
 */
public class NotTerm implements ConditionalTerm {

	public boolean eval(String... params) {
		return false;
	}

	public int numberOfParams() {
		return -1;
	}

}
