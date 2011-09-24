package org.duguo.xdir.security.api.authentication;

import org.duguo.xdir.spi.security.LoginEvent;

public interface LoginRetriever {
	void retrieve(LoginEvent loginEvent);
}
