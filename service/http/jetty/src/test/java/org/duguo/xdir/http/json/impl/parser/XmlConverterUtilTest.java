package org.duguo.xdir.http.json.impl.parser;

import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.http.json.impl.JsonArrayImpl;
import org.duguo.xdir.http.json.impl.JsonMapImpl;

import junit.framework.TestCase;

public class XmlConverterUtilTest extends TestCase {

	public void testToXmlAndJson() {
		Json json=new JsonMapImpl("_meta","<?xml version=\"1.0\"?>","feed",new JsonMapImpl("@attr","f1",
										"entry",new JsonArrayImpl(
												new JsonMapImpl("@attr","e1","text","entry text"),
												new JsonMapImpl("@attr","e2","text","entry text 2"))));
		
		String xmlString="\n<?xml version=\"1.0\"?>" +
		"\n<feed attr=\"f1\">"+
		"\n  <entry attr=\"e1\">entry text</entry>"+
		"\n  <entry attr=\"e2\">entry text 2</entry>"+
		"\n</feed>";
		
		assertEquals(xmlString, XmlConverterUtil.toXml(json));
		assertEquals("e1", XmlConverterUtil.toJson(xmlString).get("feed").get("entry").get("@attr").toString());
		
		long timestamp=System.currentTimeMillis();
		//for(int i=0;i<100000;i++){ // about 3 seconds
		for(int i=0;i<1;i++){
			XmlConverterUtil.toJson(xmlString).get("feed").get("entry").get("@attr").toString();
		}
		timestamp=System.currentTimeMillis()-timestamp;
		System.out.println("time spend:"+timestamp);
	}

	public void testEscape() {
		String unescapedString="a&b<c>d\"e";
		assertEquals("a&amp;b&lt;c&gt;d&quot;e", XmlConverterUtil.escape(unescapedString));
	}

}
