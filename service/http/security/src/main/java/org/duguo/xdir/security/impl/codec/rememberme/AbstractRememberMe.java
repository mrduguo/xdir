package org.duguo.xdir.security.impl.codec.rememberme;

import org.duguo.xdir.security.api.codec.StringEncoder;

public abstract class AbstractRememberMe implements StringEncoder {

	private StringEncoder secretCodec;

	public StringEncoder getSecretCodec() {
		return secretCodec;
	}

	public void setSecretCodec(StringEncoder secretCodec) {
		this.secretCodec = secretCodec;
	}

}
