package org.duguo.xdir.osgi.bootstrap.conditional.term;

import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.duguo.xdir.osgi.spi.conditional.ConditionalTerm;


/**
 * pass if OSGi service exist
 * 
 * Samples:
 * if__service__a.b.c.Foo
 * if__service__org.eclipse.gemini.blueprint.bean.name-u003D-fooService
 * 
 * @author mrduguo
 *
 */
public class ServiceTerm implements ConditionalTerm {
	
	public Bundle bundle;
	
	public ServiceTerm(Bundle bundle){
		this.bundle=bundle;
	}

	public boolean eval(String... params) {
		ServiceReference[] serviceReferences =null;
		try {
			if(params[0].indexOf('=')<0){ // to match class
				serviceReferences=bundle.getBundleContext().getServiceReferences(params[0], null);
			}else{ // to match by filter
				serviceReferences=bundle.getBundleContext().getServiceReferences(null,params[0]);
			}
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException("failed to verify service by:" + params[0],e);
		}
		
		if(serviceReferences.length>0){
			return true;
		}else{
			return false;
		}
	}

	public int numberOfParams() {
		return 1;
	}

}
