package org.duguo.xdir.security.impl.authentication.validator;

import org.duguo.xdir.spi.security.LoginEvent;
import org.duguo.xdir.spi.security.Role;
import org.duguo.xdir.security.api.authentication.LoginValidator;
import org.duguo.xdir.security.impl.authentication.UserImpl;


/**
 * This is for request already authorised by proxy server such apache web server
 * 
 * @author duguo
 *
 */
public class AuthorisedValidator implements LoginValidator{
	
	private int defaultRole=Role.USER;

	public int validate(LoginEvent loginEvent) {
		UserImpl user=new UserImpl();
        user.setUserId( loginEvent.getUserName() );
        loginEvent.setUser(user);
        user.setRole(defaultRole);
        if(loginEvent.getName()==null){
        	loginEvent.setName(getClass().getSimpleName());
        }
        return LoginEvent.LOGIN_SUCCESS;
	}

	public int getDefaultRole() {
		return defaultRole;
	}

	public void setDefaultRole(int defaultRole) {
		this.defaultRole = defaultRole;
	}
}
