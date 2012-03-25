package org.duguo.xdir.security.impl.authentication.authenticator;


import org.duguo.xdir.security.api.authentication.Authenticator;
import org.duguo.xdir.security.api.authentication.LoginNormalizer;
import org.duguo.xdir.security.api.authentication.LoginRetriever;
import org.duguo.xdir.security.api.authentication.LoginValidator;
import org.duguo.xdir.spi.security.LoginEvent;


public class UserNamePasswordAuthenticator implements Authenticator{
    
	private LoginRetriever[] userNamePasswordRetrievers;
    private LoginNormalizer userNamePasswordNormalizer;
	private LoginValidator userNamePasswordValidator;

	
	public int authenticate(LoginEvent loginEvent) {
		retriveUserNamePassword(loginEvent);
		if(loginEvent.getUserName()!=null){
			userNamePasswordNormalizer.normalize(loginEvent);
			if(loginEvent.getUserName()!=null){
				return userNamePasswordValidator.validate(loginEvent);
			}
		}
		return LoginEvent.LOGIN_USER_NAME_NOT_FOUND;
	}

	protected void retriveUserNamePassword(LoginEvent loginEvent) {
		for(LoginRetriever userNamePasswordRetriever:userNamePasswordRetrievers){
			userNamePasswordRetriever.retrieve(loginEvent);
			if(loginEvent.getUserName()!=null){
				break;
			}
		}
	}

	public LoginRetriever[] getUserNamePasswordRetrievers() {
		return userNamePasswordRetrievers;
	}

	public void setUserNamePasswordRetrievers(
			LoginRetriever[] userNamePasswordRetrievers) {
		this.userNamePasswordRetrievers = userNamePasswordRetrievers;
	}

	public LoginNormalizer getUserNamePasswordNormalizer() {
		return userNamePasswordNormalizer;
	}

	public void setUserNamePasswordNormalizer(
			LoginNormalizer userNamePasswordNormalizer) {
		this.userNamePasswordNormalizer = userNamePasswordNormalizer;
	}

    public LoginValidator getUserNamePasswordValidator() {
        return userNamePasswordValidator;
    }

    public void setUserNamePasswordValidator(LoginValidator userNamePasswordValidator) {
        this.userNamePasswordValidator = userNamePasswordValidator;
    }
}
