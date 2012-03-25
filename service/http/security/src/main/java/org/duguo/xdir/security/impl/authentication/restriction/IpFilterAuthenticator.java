package org.duguo.xdir.security.impl.authentication.restriction;


import org.duguo.xdir.security.api.authentication.Authenticator;
import org.duguo.xdir.spi.security.LoginEvent;
import org.duguo.xdir.spi.util.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class IpFilterAuthenticator implements Authenticator{
    
    
    private static final Logger logger = LoggerFactory.getLogger( IpFilterAuthenticator.class );

    private List<String> selectedIps;
    private boolean blocked=true;
	
	public int authenticate(LoginEvent loginEvent) {
		int result;
		String clientIp=HttpUtil.getClientIp( loginEvent.getRequest() );
		if(selectedIps.contains(clientIp )){
	        if(logger.isDebugEnabled())
                logger.debug("user login ip filter matched for ip [{}]",clientIp);
	        if(blocked){
		        result=LoginEvent.LOGIN_RESTRICTED;
	        }else{
	        	result=LoginEvent.LOGIN_USER_NAME_NOT_FOUND;
	        }
	    }else{
	    	if(blocked){
		        result=LoginEvent.LOGIN_USER_NAME_NOT_FOUND;
	        }else{
	        	result=LoginEvent.LOGIN_RESTRICTED;
	        }
	    }
		return result;
	}

	public List<String> getSelectedIps() {
		return selectedIps;
	}

	public void setSelectedIps(List<String> selectedIps) {
		this.selectedIps = selectedIps;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean isBlocked) {
		this.blocked = isBlocked;
	}

	
}
