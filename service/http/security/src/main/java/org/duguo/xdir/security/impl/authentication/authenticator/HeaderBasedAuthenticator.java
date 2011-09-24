package org.duguo.xdir.security.impl.authentication.authenticator;

import org.duguo.xdir.spi.security.LoginEvent;
import org.duguo.xdir.security.api.authentication.Authenticator;
import org.duguo.xdir.security.api.authentication.LoginRetriever;
import org.duguo.xdir.security.api.authentication.LoginValidator;

public class HeaderBasedAuthenticator implements Authenticator{

    private LoginRetriever loginRetriever;
    private LoginValidator loginValidator;
	
	public int authenticate(LoginEvent loginEvent) {
		int result=LoginEvent.LOGIN_USER_NAME_NOT_FOUND;
		loginRetriever.retrieve(loginEvent);
		if(loginEvent.getUserName()!=null){
			result=loginValidator.validate(loginEvent);
		}
	    return result;
	}

	public LoginRetriever getLoginRetriever() {
		return loginRetriever;
	}

	public void setLoginRetriever(LoginRetriever loginRetriever) {
		this.loginRetriever = loginRetriever;
	}

	public LoginValidator getLoginValidator() {
		return loginValidator;
	}

	public void setLoginValidator(LoginValidator loginValidator) {
		this.loginValidator = loginValidator;
	}

	
}
