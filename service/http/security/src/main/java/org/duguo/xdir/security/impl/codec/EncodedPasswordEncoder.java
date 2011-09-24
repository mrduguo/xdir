package org.duguo.xdir.security.impl.codec;

import org.duguo.xdir.spi.security.LoginEvent;
import org.duguo.xdir.security.api.codec.StringEncoder;

public class EncodedPasswordEncoder extends UserNamePlusPasswordPasswordEncoder{
	private StringEncoder stringEncoder;
	
	public String encode(LoginEvent loginEvent){
		return stringEncoder.encode(super.encode(loginEvent));
	}

	public StringEncoder getStringEncoder() {
		return stringEncoder;
	}

	public void setStringEncoder(StringEncoder stringEncoder) {
		this.stringEncoder = stringEncoder;
	}
}
