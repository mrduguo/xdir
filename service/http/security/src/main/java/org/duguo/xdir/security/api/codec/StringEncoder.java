package org.duguo.xdir.security.api.codec;

public interface StringEncoder {

	public static final String UNICODE_FORMAT = "UTF8";
	
	public String encode(String inputStr);
}
