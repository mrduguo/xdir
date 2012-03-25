package org.duguo.xdir.http.json.impl.parser;

import junit.framework.TestCase;
import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.http.json.impl.JsonArrayImpl;
import org.duguo.xdir.http.json.impl.JsonMapImpl;

public class JsonConverterUtilTest extends TestCase {

	public void testToTextAndJson() {
		Json json=new JsonMapImpl("string","string value","entry",new JsonArrayImpl(1));
		String jsonString="\n{\n  \"string\":\"string value\",\n  \"entry\":\n    [\n      1\n    ]\n}";
		
		assertEquals(jsonString, JsonConverterUtil.toText(json));
		assertEquals(json.toString(), JsonConverterUtil.toJson(jsonString).toString());
	}

	public void testEscape() {
		String unescapedString="a\"b";
		assertEquals("a\\\"b", JsonConverterUtil.escape(unescapedString));
	}

}
