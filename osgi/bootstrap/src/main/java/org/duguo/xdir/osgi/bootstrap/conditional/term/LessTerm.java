package org.duguo.xdir.osgi.bootstrap.conditional.term;

/**
 * pass if the system property value less than the expected string
 * 
 * Expected string will be parsed as float, if failed to do so, will do simple string compare
 * 
 * Samples:
 * if__java.specification.version__less__1.5
 * if__os.name__less__Linux
 * 
 * @author mrduguo
 *
 */
public class LessTerm extends GreaterTerm {

	protected boolean verifyPair(String left, String right) {
		return super.verifyPair(right,left);
	}

}
