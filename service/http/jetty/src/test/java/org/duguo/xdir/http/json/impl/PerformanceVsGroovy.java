package org.duguo.xdir.http.json.impl;

public class PerformanceVsGroovy {

}

/*

import groovy.util.Node;
import groovy.util.NodeList;
import groovy.util.XmlParser;

import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.http.json.impl.parser.XmlConverterUtil;

public class Tester {

	public  static void main(String...strings ) throws Exception{

		String xmlString="<?xml version=\"1.0\"?>" +
		"\n<feed attr=\"f1\">"+
		"\n  <entry attr=\"e1\">entry text</entry>"+
		"\n  <entry attr=\"e2\">entry text 2</entry>"+
		"\n</feed>";
		
		Json json=XmlConverterUtil.toJson(xmlString);
		long timestamp=System.currentTimeMillis();
		for(int i=0;i<100000000;i++){
			//XmlConverterUtil.toJson(xmlString);
			//XmlConverterUtil.toJson(xmlString).get("feed").get("entry").get("@attr").toString();
			json.get("feed").get("entry").get("@attr").toString();
		}
		timestamp=System.currentTimeMillis()-timestamp;
		System.out.println("time spend:"+timestamp);
		
		
		Node node = new XmlParser().parseText(xmlString);
		timestamp=System.currentTimeMillis();
		for(int i=0;i<100000000;i++){
			//new XmlParser().parseText(xmlString);
			//((Node)((NodeList)new XmlParser().parseText(xmlString).get("entry")).get(0)).get("@attr");
			((Node)((NodeList)node.get("entry")).get(0)).get("@attr");
		}
		timestamp=System.currentTimeMillis()-timestamp;
		System.out.println("time spend:"+timestamp);
		
		
	}
}	
*/
