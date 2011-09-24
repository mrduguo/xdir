package org.duguo.xdir.security.impl.authentication.authenticator;


import org.duguo.xdir.spi.security.LoginEvent;
import org.duguo.xdir.security.api.authentication.Authenticator;
import org.duguo.xdir.security.api.authentication.LoginNormalizer;
import org.duguo.xdir.security.api.authentication.LoginRetriever;
import org.duguo.xdir.security.api.authentication.LoginValidator;


public class UserNamePasswordAuthenticator implements Authenticator{
    
	private LoginRetriever[] userNamePasswordRetrievers;
    private LoginNormalizer userNamePasswordNormalizer;
	private LoginValidator[] userNamePasswordValidators;

	
	public int authenticate(LoginEvent loginEvent) {
		retriveUserNamePassword(loginEvent);
		if(loginEvent.getUserName()!=null){
			userNamePasswordNormalizer.normalize(loginEvent);
			if(loginEvent.getUserName()!=null){
				return validateUserNamePassword(loginEvent);
			}
		}
		return LoginEvent.LOGIN_USER_NAME_NOT_FOUND;
	}

	protected int validateUserNamePassword(LoginEvent loginEvent) {
		int result=LoginEvent.LOGIN_USER_NAME_NOT_FOUND;
		for(LoginValidator usernamePasswordValidator:userNamePasswordValidators){
			result=usernamePasswordValidator.validate(loginEvent);
			if(result!=LoginEvent.LOGIN_USER_NAME_NOT_FOUND){
				break;
			}
		}
		return result;
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

	public LoginValidator[] getUserNamePasswordValidators() {
		return userNamePasswordValidators;
	}

	public void setUserNamePasswordValidators(
			LoginValidator[] userNamePasswordValidators) {
		this.userNamePasswordValidators = userNamePasswordValidators;
	}

	
}
