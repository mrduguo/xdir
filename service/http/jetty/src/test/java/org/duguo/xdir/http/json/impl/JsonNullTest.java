package org.duguo.xdir.http.json.impl;

import junit.framework.TestCase;

public class JsonNullTest extends TestCase {

	private JsonNull json;
	
	protected void setUp() throws Exception {
		json=(JsonNull)JsonNull.instance();
	}

	public void testToString() {
		assertNull(json.toString());
	}

	public void testInstance() {
		assertEquals(JsonNull.instance(), json);
	}

	public void testIsNull() {
		assertTrue(json.isNull());
	}

}
