package org.duguo.xdir.security.impl.authentication.restriction;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.spi.security.LoginEvent;
import org.duguo.xdir.spi.security.Role;
import org.duguo.xdir.security.api.authentication.Authenticator;

public class RoleRequiredAuthenticator implements Authenticator{
    
    
    private static final Logger logger = LoggerFactory.getLogger( RoleRequiredAuthenticator.class );

    private int requiredRole=Role.USER;
	
	public int authenticate(LoginEvent loginEvent) {
		int result;
		if(loginEvent.getUser().getRole()>=requiredRole){
	        result=LoginEvent.LOGIN_SUCCESS; 
		}else{
	        result=LoginEvent.LOGIN_RESTRICTED;
	        if(logger.isDebugEnabled())
	        	logger.debug("user [{}] role [{}] restricted",loginEvent.getUser().getUserId(),loginEvent.getUser().getRole());
		}
		return result;
	}

	public int getRequiredRole() {
		return requiredRole;
	}

	public void setRequiredRole(int requiredRole) {
		this.requiredRole = requiredRole;
	}


	
}
