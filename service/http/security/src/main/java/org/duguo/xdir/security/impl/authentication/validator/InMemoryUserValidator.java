package org.duguo.xdir.security.impl.authentication.validator;

import org.duguo.xdir.security.api.authentication.LoginValidator;
import org.duguo.xdir.security.api.codec.PasswordEncoder;
import org.duguo.xdir.security.impl.authentication.UserImpl;
import org.duguo.xdir.spi.security.LoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryUserValidator implements LoginValidator{
	
    private static final Logger logger = LoggerFactory.getLogger( InMemoryUserValidator.class );

	private UserImpl[] users;
	private PasswordEncoder passwordEncoder;
	

	public int validate(LoginEvent loginEvent) {
		for(UserImpl user: users){
			if(user.getUserName().equals(loginEvent.getUserName())){
                if(loginEvent.getPassword()==null){
                    if(logger.isDebugEnabled())
                        logger.debug("logged in without password success for [{}]",loginEvent.getUserName());
                }else{
                    String encodedPassword=passwordEncoder.encode(loginEvent);
                    if(!user.getPassword().equals(encodedPassword)){
                        return LoginEvent.LOGIN_WRONG_PASSWORD;
                    }
                    if(logger.isDebugEnabled())
                        logger.debug("logged in success for [{}]",loginEvent.getUserName());
                }
                loginEvent.setUser(user);
                loginEvent.setName(getClass().getSimpleName());
                return LoginEvent.LOGIN_SUCCESS;
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
