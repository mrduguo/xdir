package org.duguo.xdir.security.api.authentication;

import org.duguo.xdir.spi.security.LoginEvent;

public interface LoginValidator {
    int validate(LoginEvent loginEvent);
}
