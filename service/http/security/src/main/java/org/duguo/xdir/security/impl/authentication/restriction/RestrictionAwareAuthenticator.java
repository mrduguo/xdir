package org.duguo.xdir.security.impl.authentication.restriction;

import org.duguo.xdir.security.api.authentication.Authenticator;
import org.duguo.xdir.security.impl.authentication.authenticator.ChainedAuthenticator;
import org.duguo.xdir.spi.security.LoginEvent;

public class RestrictionAwareAuthenticator extends ChainedAuthenticator{
    
    private Authenticator[] preRestrictions;
    
    private Authenticator[] postRestrictions;
	
	public int authenticate(LoginEvent loginEvent) {
		int result = doPreCheck(loginEvent);
		
        if(result!=LoginEvent.LOGIN_RESTRICTED){
        	result = super.authenticate(loginEvent);
        	if(result==LoginEvent.LOGIN_SUCCESS){
        		result = doPostCheck(loginEvent);
        	}
        }
		
	    return result;
	}

	protected int doPreCheck(LoginEvent loginEvent) {
		int result=LoginEvent.LOGIN_USER_NAME_NOT_FOUND;
		if(preRestrictions!=null){
		    for(Authenticator authenticator:preRestrictions){
		    	result=authenticator.authenticate( loginEvent );
		        if(result==LoginEvent.LOGIN_RESTRICTED){
		            break;
		        }
		    }
		}
		return result;
	}

	protected int doPostCheck(LoginEvent loginEvent) {
		int result=LoginEvent.LOGIN_SUCCESS;
		if(postRestrictions!=null){
		    for(Authenticator authenticator:postRestrictions){
		    	result=authenticator.authenticate( loginEvent );
		        if(result==LoginEvent.LOGIN_RESTRICTED){
		            break;
		        }
		    }
		}
		return result;
	}

	public Authenticator[] getPreRestrictions() {
		return preRestrictions;
	}


	public void setPreRestrictions(Authenticator[] preRestrictions) {
		this.preRestrictions = preRestrictions;
	}


	public Authenticator[] getPostRestrictions() {
		return postRestrictions;
	}


	public void setPostRestrictions(Authenticator[] postRestrictions) {
		this.postRestrictions = postRestrictions;
	}
	
}
