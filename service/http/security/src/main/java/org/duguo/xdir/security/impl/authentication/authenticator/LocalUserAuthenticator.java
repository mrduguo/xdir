package org.duguo.xdir.security.impl.authentication.authenticator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.spi.security.LoginEvent;
import org.duguo.xdir.spi.security.Role;
import org.duguo.xdir.security.api.authentication.Authenticator;
import org.duguo.xdir.security.impl.authentication.UserImpl;
import org.duguo.xdir.util.http.HttpUtil;

import java.util.Set;

public class LocalUserAuthenticator implements Authenticator{
    
    
    private static final Logger logger = LoggerFactory.getLogger( LocalUserAuthenticator.class );

	private Set<String> localIpAddress;
	private String userName;
	
	public int authenticate(LoginEvent loginEvent) {
        if(logger.isTraceEnabled()) logger.trace("> authenticate remote address {}", loginEvent.getRequest().getRemoteAddr());
		int result=LoginEvent.LOGIN_USER_NAME_NOT_FOUND;
		if(localIpAddress.contains(loginEvent.getRequest().getRemoteAddr() )){
	        UserImpl user=new UserImpl();
	        user.setUserId( userName );
	        user.setRole(Role.ADMIN);
	        
	        loginEvent.setUser(user);
	        loginEvent.setUserName(userName);
	        loginEvent.setName(getClass().getSimpleName());
	        
	        if(logger.isInfoEnabled())  logger.info("auto logged in as local user [{}]",userName);
	        result=LoginEvent.LOGIN_SUCCESS;
	    }
        if(logger.isTraceEnabled()) logger.trace("< authenticate  {}", result);
        return result;
	}

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setLocalIpAddress(Set<String> localIpAddress) {
        this.localIpAddress = localIpAddress;
    }
}
