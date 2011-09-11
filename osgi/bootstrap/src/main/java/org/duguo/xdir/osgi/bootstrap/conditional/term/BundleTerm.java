package org.duguo.xdir.osgi.bootstrap.conditional.term;

import org.osgi.framework.Bundle;
import org.duguo.xdir.osgi.spi.conditional.ConditionalTerm;

/**
 * pass if OSGi bundle exist
 * 
 * Samples:
 * if__bundle__a.b.c
 * if__bundle__a.b.c-1.2.3
 * 
 * @author mrduguo
 *
 */
public class BundleTerm implements ConditionalTerm {

	public Bundle bundle;

	public BundleTerm(Bundle bundle) {
		this.bundle = bundle;
	}

	public boolean eval(String... params) {
		for (Bundle currentBundle : bundle.getBundleContext().getBundles()) {
			String bundleKey = currentBundle.getSymbolicName() + "-"+ currentBundle.getVersion();
			if (bundleKey.startsWith(params[0])) {
				return true;
			}
		}
		return false;
	}

	public int numberOfParams() {
		return 1;
	}

}
