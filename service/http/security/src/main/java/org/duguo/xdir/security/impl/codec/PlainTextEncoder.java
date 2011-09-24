package org.duguo.xdir.security.impl.codec;

import org.duguo.xdir.security.api.codec.StringEncoder;

public class PlainTextEncoder implements StringEncoder {

	public String encode(String inputStr) {
		return inputStr;
	}

}
