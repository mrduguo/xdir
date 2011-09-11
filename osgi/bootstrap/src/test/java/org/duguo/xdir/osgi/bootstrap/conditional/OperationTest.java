package org.duguo.xdir.osgi.bootstrap.conditional;

import junit.framework.TestCase;

public class OperationTest extends TestCase {
	
	public ConditionalServiceImpl conditionalServiceImpl;

	protected void setUp() throws Exception {
		super.setUp();
		conditionalServiceImpl=new ConditionalServiceImpl();
	}

	public void testNotAndOr() {
		String osName=System.getProperty("os.name");
		assertFalse(conditionalServiceImpl.eval("if__os.name__not__equals__"+osName));
		assertTrue(conditionalServiceImpl.eval("if__os.name__equals__"+osName));
		assertTrue(conditionalServiceImpl.eval("if__os.name__equals__"+osName+"__extra_to_be_ignored"));
		
		assertFalse(conditionalServiceImpl.eval("if__os.name__equals__"+osName+"__and__os.name__not__equals__"+osName));
		assertTrue(conditionalServiceImpl.eval("if__os.name__not__equals__"+osName+"__or__os.name__not__equals__"+osName+"__or__os.name__equals__"+osName));
		assertTrue(conditionalServiceImpl.eval("if__os.name__equals__"+osName+"__and__os.name__equals__"+osName+"__and__os.name__equals__"+osName));
		
	}

}
