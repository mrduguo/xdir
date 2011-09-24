package org.duguo.xdir.security.api.authentication;

import org.duguo.xdir.spi.security.LoginEvent;



public interface Authenticator {
    int authenticate(LoginEvent loginEvent);
}
