package org.duguo.xdir.security.api.authentication;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duguo.xdir.spi.security.LoginEvent;
import org.duguo.xdir.spi.security.User;


public interface LoginManager {
    
    /**
     * retrieve user from session if authenticated, otherwise return null
     * 
     * @param model
     * @return user in session or null if not exist
     */
    User getUserFromSession( HttpServletRequest request );
    
    /**
     * store authenticated user into session, setup user info cookie for browser
     * 
     * @param model
     * @param user
     */
	void login( LoginEvent loginEvent,HttpServletResponse response);
	
	/**
	 * remove user from session and reset user info cookie
	 * @param model
	 */
	void logout(HttpServletRequest request,HttpServletResponse response);
}
