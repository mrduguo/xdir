package org.duguo.xdir.osgi.bootstrap.conditional.term;

import org.duguo.xdir.osgi.spi.conditional.ConditionalTerm;


/**
 * operator for AND
 * 
 * Samples:
 * if__windows__and__linux
 * 
 * @author mrduguo
 *
 */
public class AndTerm implements ConditionalTerm {

	public boolean eval(String... params) {
		return false;
	}

	public int numberOfParams() {
		return -1;
	}

}
