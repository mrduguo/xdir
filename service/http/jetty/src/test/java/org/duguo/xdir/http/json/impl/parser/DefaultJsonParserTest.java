package org.duguo.xdir.http.json.impl.parser;

import junit.framework.TestCase;

import java.io.IOException;

public class DefaultJsonParserTest extends TestCase {
	
	private String inputString;
	private DefaultJsonTextConverter parser;

	protected void setUp() throws Exception {
		parser=new DefaultJsonTextConverter();
	}

	public void testSuccessParse() throws IOException {

		
		inputString = "[{\"first_name\":\"Guo\", \"uid\":1628168014}]";
		inputString = "{\"first_name\":\"Guo\", \"uid\":1628168014, \"array\":[{\"first_name\":\"Guo\", \"uid\":1628168014}]}";
		assertEquals(inputString,parser.parse(inputString).toString());
		//if(true) return;
		
		inputString = "[\"JSON\", 1, 2.0, true]";
		inputString = "[{'first_name':'Guo','last_name':'Du','uid':1628168014,'pic_square':'http://profile.ak.fbcdn.net/v225/1098/71/q1628168014_5858.jpg','username':'mrduguo','email':'online@duguo.com'}]";
		inputString = "{\"first_name\":\"Guo\", \"uid\":1628168014}";
		assertEquals(inputString,parser.parse(inputString).toString());
		
		inputString = "{\"uid\":1628168014, \"first_name\":\"Guo\"}";
		assertEquals(inputString,parser.parse(inputString).toString());
		
		inputString = "{\"first_name\":\"Guo\", \"uid\":1628168014, \"array\":[{\"first_name\":\"Guo\", \"uid\":1628168014}]}";
		assertEquals(inputString,parser.parse(inputString).toString());
		
		inputString = "[{\"first_name\":\"Guo\", \"uid\":1628168014}]";
		assertEquals(inputString,parser.parse(inputString).toString());

		
		inputString = "[\"JSON\", 1, 2.0, true]";
		String inputString2 = "['JSON\', 1, 2.0, true]";
		assertEquals(parser.parse(inputString2).toString(),parser.parse(inputString).toString());
		
	}

	
	public void testFailureParse() throws IOException {
		
		inputString = "[\"JSON\", 1, 2.0, true,'aa";
		try{
			assertEquals(inputString,parser.parse(inputString).toString());
			assertFalse("expect exception here",true);
		}catch(Exception ex){
		}		
	}

}
