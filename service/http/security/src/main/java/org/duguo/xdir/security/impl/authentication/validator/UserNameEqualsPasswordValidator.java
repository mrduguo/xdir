package org.duguo.xdir.security.impl.authentication.validator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.spi.security.LoginEvent;
import org.duguo.xdir.spi.security.Role;
import org.duguo.xdir.security.api.authentication.LoginValidator;
import org.duguo.xdir.security.impl.authentication.UserImpl;

/**
 * This validator is for test purpose, it skip the validation if userName 
 * and password doesn't match which will result validation failure for authenticator.
 * 
 * @author duguo
 *
 */
public class UserNameEqualsPasswordValidator implements LoginValidator
{
    
    private static final Logger logger = LoggerFactory.getLogger( UserNameEqualsPasswordValidator.class );

	private int defaultRole=Role.USER;
	
    public int validate(LoginEvent loginEvent)
    {
        if ( loginEvent.getUserName().equals( loginEvent.getPassword() ) )
        {
            UserImpl user=new UserImpl();
            user.setUserId( loginEvent.getUserName() );
            loginEvent.setUser(user);
	        user.setRole(defaultRole);
            loginEvent.setName(getClass().getSimpleName());
            
            if(logger.isDebugEnabled())
                logger.debug("userName equals password logged in as [{}]",loginEvent.getUserName());
            return LoginEvent.LOGIN_SUCCESS;
        }
        else
        {
            return LoginEvent.LOGIN_WRONG_PASSWORD;
        }
    }

	public int getDefaultRole() {
		return defaultRole;
	}

	public void setDefaultRole(int defaultRole) {
		this.defaultRole = defaultRole;
	}
}
