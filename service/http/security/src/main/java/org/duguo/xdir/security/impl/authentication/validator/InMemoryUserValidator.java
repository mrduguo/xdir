package org.duguo.xdir.security.impl.authentication.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.spi.security.LoginEvent;
import org.duguo.xdir.security.api.authentication.LoginValidator;
import org.duguo.xdir.security.api.codec.PasswordEncoder;
import org.duguo.xdir.security.impl.authentication.UserImpl;

public class InMemoryUserValidator implements LoginValidator{
	
    private static final Logger logger = LoggerFactory.getLogger( InMemoryUserValidator.class );

	private UserImpl[] users;
	private PasswordEncoder passwordEncoder;
	

	public int validate(LoginEvent loginEvent) {
		for(UserImpl user: users){
			if(user.getUserName().equals(loginEvent.getUserName())){
				String encodedPassword=passwordEncoder.encode(loginEvent);
				if(user.getPassword().equals(encodedPassword)){
					loginEvent.setUser(user);
			        loginEvent.setName(getClass().getSimpleName());
			        if(logger.isDebugEnabled())
			        	logger.debug("logged in success for [{}]",loginEvent.getUserName());
			        return LoginEvent.LOGIN_SUCCESS;
				}else{
			        return LoginEvent.LOGIN_WRONG_PASSWORD;
				}
			}
		}
        return LoginEvent.LOGIN_USER_NAME_NOT_FOUND;
	}

	public UserImpl[] getUsers() {
		return users;
	}

	public void setUsers(UserImpl[] users) {
		this.users = users;
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
}
