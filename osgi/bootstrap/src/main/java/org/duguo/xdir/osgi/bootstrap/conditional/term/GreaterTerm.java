package org.duguo.xdir.osgi.bootstrap.conditional.term;

/**
 * pass if the system property value greater than the expected string
 * 
 * Expected string will be parsed as float, if failed to do so, will do simple string compare
 * 
 * Samples:
 * if__java.specification.version__greater__1.5
 * if__os.name__greater__Linux
 * 
 * @author mrduguo
 *
 */
public class GreaterTerm extends EqualsTerm {

	protected boolean verifyPair(String left, String right) {
		try {
			float leftValue = Float.parseFloat(left);
			float rightValue = Float.parseFloat(right);
			return leftValue > rightValue;
		} catch (NumberFormatException ex) {
			return left.compareTo(right) > 0;
		}
	}

}
