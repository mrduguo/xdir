package org.duguo.xdir.http.json.impl;

import org.duguo.xdir.http.json.impl.JsonArrayImpl;
import org.duguo.xdir.http.json.impl.JsonMapImpl;
import org.duguo.xdir.http.json.impl.JsonNull;

import junit.framework.TestCase;

public class AbstractJsonTest extends TestCase {

	private JsonMapImpl json;
	
	protected void setUp() throws Exception {
		json =new JsonMapImpl();
	}

	public void testArray() {
		assertFalse(json.isArray());
		assertTrue(json.array().isNull());
	}

	public void testMap() {
		assertTrue(json.isMap());
		assertFalse(json.map().isNull());
		
		JsonArrayImpl jsonArray =new JsonArrayImpl();
		assertFalse(jsonArray.isMap());
		assertTrue(jsonArray.map().isNull());
	}

	public void testValue() {
		assertFalse(json.isValue());
		assertTrue(json.value().isNull());
	}

	public void testParent() {
		assertTrue(json.parent().isNull());
		json.parent(json);
		assertEquals(json.parent(),json);
	}

	public void testToString() {
		assertEquals("{}",json.toString());
		json.set("stringkey", "stringvalue");
		json.set("intkey", Integer.MAX_VALUE);
		json.set("longkey", Long.MAX_VALUE);
		json.set("doublekey", Double.MAX_VALUE);
		json.set("booleankey", Boolean.TRUE);
		json.set("arraykey", new JsonArrayImpl("stringvalue",Integer.MAX_VALUE));
		json.set("nullkey", JsonNull.instance());
		assertEquals("{\"stringkey\":\"stringvalue\", \"intkey\":2147483647, \"longkey\":9223372036854775807, \"doublekey\":1.7976931348623157E308, \"booleankey\":true, \"arraykey\":[\"stringvalue\", 2147483647]}",json.toString());
	}

}
