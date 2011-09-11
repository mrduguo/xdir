package org.duguo.xdir.osgi.bootstrap.conditional;

import junit.framework.TestCase;

public class TermTest extends TestCase {
	
	public ConditionalServiceImpl conditionalServiceImpl;

	protected void setUp() throws Exception {
		super.setUp();
		conditionalServiceImpl=new ConditionalServiceImpl();
	}

	public void testOs() {
		String osName=System.getProperty("os.name");
		if(osName.equals("Windows")){
			assertTrue(conditionalServiceImpl.eval("if__windows"));
		}else{
			assertTrue(conditionalServiceImpl.eval("if__not__windows"));
			assertFalse(conditionalServiceImpl.eval("if__windows"));			
		}
		
		if(osName.equals("Linux")){
			assertTrue(conditionalServiceImpl.eval("if__linux"));
		}else{
			assertTrue(conditionalServiceImpl.eval("if__not__linux"));
			assertFalse(conditionalServiceImpl.eval("if__linux"));
		}
	}

	public void testEquals() {
		String osName=System.getProperty("os.name");
		assertTrue(conditionalServiceImpl.eval("if__os.name__equals__"+osName));
		assertFalse(conditionalServiceImpl.eval("if__os.name__not__equals__"+osName));
	}

	public void testCompare() {
		System.setProperty("test.float","10.5");
		assertTrue(conditionalServiceImpl.eval("if__test.float__equals__10.5"));
		assertFalse(conditionalServiceImpl.eval("if__test.float__greater__10.5"));
		assertFalse(conditionalServiceImpl.eval("if__test.float__less__10.5"));
		assertTrue(conditionalServiceImpl.eval("if__test.float__less__11"));
		assertTrue(conditionalServiceImpl.eval("if__test.float__greater__10"));
	}

	public void testContains() {
		System.setProperty("test.contains","abcdefg");
		assertTrue(conditionalServiceImpl.eval("if__test.contains__equals__abcdefg"));
		assertTrue(conditionalServiceImpl.eval("if__test.contains__contains__abcdefg"));
		assertFalse(conditionalServiceImpl.eval("if__test.contains__contains__xyz"));
		assertTrue(conditionalServiceImpl.eval("if__test.contains__contains__abc"));
		assertTrue(conditionalServiceImpl.eval("if__test.contains__contains__efg"));
	}

	public void testExists() {
		assertTrue(conditionalServiceImpl.eval("if__exists__-u002F-"));
		assertFalse(conditionalServiceImpl.eval("if__exists__-u002F-"+System.currentTimeMillis()));
	}

	public void testParamDecoder() {
		String osName=System.getProperty("os.name");
		System.setProperty("test.string","os.name:"+osName);
		assertTrue(conditionalServiceImpl.eval("if__test.string__equals__os.name-u003A--u0024--u007B-os.name-u007D-")); //if__test.string__equals__os.name:${os.name}
	}

}
