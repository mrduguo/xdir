package org.duguo.xdir.security.impl.codec;

import org.duguo.xdir.security.api.codec.StringEncoder;

public class SaltEnabledEncoder implements StringEncoder{

	private String salt;
	private StringEncoder stringEncoder;
	
	public String encode(String inputStr) {
		return stringEncoder.encode(salt+inputStr);
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public StringEncoder getStringEncoder() {
		return stringEncoder;
	}

	public void setStringEncoder(StringEncoder stringEncoder) {
		this.stringEncoder = stringEncoder;
	}	

}
