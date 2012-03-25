package org.duguo.xdir.security.impl.codec;

import org.duguo.xdir.security.api.codec.PasswordEncoder;
import org.duguo.xdir.spi.security.LoginEvent;

public class UserNamePlusPasswordPasswordEncoder implements PasswordEncoder{
	public String encode(LoginEvent loginEvent){
		return loginEvent.getUserName()+loginEvent.getPassword();
	}
}
