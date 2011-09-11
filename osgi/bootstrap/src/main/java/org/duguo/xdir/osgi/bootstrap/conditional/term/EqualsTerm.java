package org.duguo.xdir.osgi.bootstrap.conditional.term;

import org.duguo.xdir.osgi.spi.conditional.ConditionalTerm;

/**
 * pass if the system property value equals the expected string
 * 
 * Samples:
 * if__os.name__equals__Windows
 * 
 * @author mrduguo
 *
 */
public class EqualsTerm implements ConditionalTerm {

	public boolean eval(String... params) {
		assert params.length == 2;
		String propertyValue = System.getProperty(params[0]);
		if (propertyValue!=null && verifyPair(propertyValue,params[1])) {
			return true;
		}
		return false;
	}

	protected boolean verifyPair(String left, String right) {
		return left.equals(right);
	}

	public int numberOfParams() {
		return 2;
	}

}
