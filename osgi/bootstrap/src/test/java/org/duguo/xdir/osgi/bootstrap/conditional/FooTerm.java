package org.duguo.xdir.osgi.bootstrap.conditional;

import org.duguo.xdir.osgi.spi.conditional.ConditionalTerm;

public class FooTerm implements ConditionalTerm {

	public boolean eval(String... params) {
		return params[0].equals(params[1]);
	}

	public int numberOfParams() {
		return 2;
	}

}
