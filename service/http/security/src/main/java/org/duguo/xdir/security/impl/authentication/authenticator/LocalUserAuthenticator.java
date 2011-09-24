package org.duguo.xdir.security.impl.authentication.authenticator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.spi.security.LoginEvent;
import org.duguo.xdir.spi.security.Role;
import org.duguo.xdir.security.api.authentication.Authenticator;
import org.duguo.xdir.security.impl.authentication.UserImpl;
import org.duguo.xdir.util.http.HttpUtil;

public class LocalUserAuthenticator implements Authenticator{
    
    
    private static final Logger logger = LoggerFactory.getLogger( LocalUserAuthenticator.class );

    private String localIpAddress="127.0.0.1";
	private int defaultRole=Role.ADMIN;
	
	public int authenticate(LoginEvent loginEvent) {
		int result=LoginEvent.LOGIN_USER_NAME_NOT_FOUND;
		if(localIpAddress.equals( HttpUtil.getClientIp( loginEvent.getRequest() ) )){
	        String userName=System.getProperty( "user.name" );
	        UserImpl user=new UserImpl();
	        user.setUserId( userName );
	        user.setRole(defaultRole);
	        
	        loginEvent.setUser(user);
	        loginEvent.setUserName(userName);
	        loginEvent.setName(getClass().getSimpleName());
	        
	        if(logger.isDebugEnabled())
                logger.debug("auto logged in as local user [{}]",userName);
	        result=LoginEvent.LOGIN_SUCCESS;
	    }
		return result;
	}

	public String getLocalIpAddress() {
		return localIpAddress;
	}

	public void setLocalIpAddress(String localIpAddress) {
		this.localIpAddress = localIpAddress;
	}

	public int getDefaultRole() {
		return defaultRole;
	}

	public void setDefaultRole(int defaultRole) {
		this.defaultRole = defaultRole;
	}
	
}
