package org.duguo.xdir.security.impl.authentication.authenticator;

import org.duguo.xdir.spi.security.LoginEvent;
import org.duguo.xdir.security.api.authentication.Authenticator;

public class ChainedAuthenticator implements Authenticator{

    private Authenticator[] authenticators;
	
	public int authenticate(LoginEvent loginEvent) {
		int result=LoginEvent.LOGIN_USER_NAME_NOT_FOUND;
	    for(Authenticator authenticator:authenticators){
	    	result=authenticator.authenticate( loginEvent );
	        if(result!=LoginEvent.LOGIN_USER_NAME_NOT_FOUND){
	            break;
	        }
	    }
	    return result;
	}
	

    public Authenticator[] getAuthenticators()
    {
        return authenticators;
    }

    public void setAuthenticators( Authenticator[] authenticators )
    {
        this.authenticators = authenticators;
    }
	
}
