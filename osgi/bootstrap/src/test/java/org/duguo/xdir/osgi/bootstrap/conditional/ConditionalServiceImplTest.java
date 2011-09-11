package org.duguo.xdir.osgi.bootstrap.conditional;

import org.duguo.xdir.osgi.bootstrap.conditional.term.EqualsTerm;

import junit.framework.TestCase;

public class ConditionalServiceImplTest extends TestCase {
	
	public ConditionalServiceImpl conditionalServiceImpl;

	protected void setUp() throws Exception {
		super.setUp();
		conditionalServiceImpl=new ConditionalServiceImpl();
	}

	public void testIsConditionalString() {
		assertFalse(conditionalServiceImpl.isConditionalString("not__linux"));
		assertFalse(conditionalServiceImpl.isConditionalString("if_linux"));
		assertTrue(conditionalServiceImpl.isConditionalString("if__linux"));
		
		System.setProperty(ConditionalServiceImpl.KEY_XDIR_OSGI_CONDITIONAL_PREFIX,"fi-");
		conditionalServiceImpl=new ConditionalServiceImpl();
		assertFalse(conditionalServiceImpl.isConditionalString("if__linux"));
		assertTrue(conditionalServiceImpl.isConditionalString("fi-linux"));
		
		System.clearProperty(ConditionalServiceImpl.KEY_XDIR_OSGI_CONDITIONAL_PREFIX);
		conditionalServiceImpl=new ConditionalServiceImpl();
		assertTrue(conditionalServiceImpl.isConditionalString("if__linux"));
		
		System.setProperty(ConditionalServiceImpl.KEY_XDIR_OSGI_CONDITIONAL_TERM_SPLITER,"-");
		conditionalServiceImpl=new ConditionalServiceImpl();
		assertFalse(conditionalServiceImpl.isConditionalString("if__linux"));
		assertTrue(conditionalServiceImpl.isConditionalString("if-linux"));
		
		System.clearProperty(ConditionalServiceImpl.KEY_XDIR_OSGI_CONDITIONAL_TERM_SPLITER);
		conditionalServiceImpl=new ConditionalServiceImpl();
		assertTrue(conditionalServiceImpl.isConditionalString("if__linux"));
	}

	public void testRegistTerm() {
		String osName=System.getProperty("os.name");
		assertFalse(conditionalServiceImpl.eval("if__os.name__eq__"+osName));
		assertFalse(conditionalServiceImpl.eval("if__abc__foo__abc"));
		
		
		conditionalServiceImpl.registTerm("eq", new EqualsTerm());
		assertTrue(conditionalServiceImpl.eval("if__os.name__eq__"+osName));
		
		
		conditionalServiceImpl.registTerm("foo", new FooTerm());
		assertTrue(conditionalServiceImpl.eval("if__abc__foo__abc"));
	}

}
