package org.duguo.xdir.security.api.codec;

import org.duguo.xdir.spi.security.LoginEvent;

public interface PasswordEncoder {
	public String encode(LoginEvent loginEvent);
}
