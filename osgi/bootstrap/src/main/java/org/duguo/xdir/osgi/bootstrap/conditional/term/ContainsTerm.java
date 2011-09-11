package org.duguo.xdir.osgi.bootstrap.conditional.term;

/**
 * pass if the system property value contain the expected string
 * 
 * Samples:
 * if__os.name__contains__Win
 * 
 * @author mrduguo
 *
 */
public class ContainsTerm extends EqualsTerm {
	
	protected boolean verifyPair(String left, String right) {
		return left.indexOf(right)>=0;
	}

}
