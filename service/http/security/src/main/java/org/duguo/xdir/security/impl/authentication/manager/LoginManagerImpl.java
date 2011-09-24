package org.duguo.xdir.security.impl.authentication.manager;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.spi.security.AccountService;
import org.duguo.xdir.spi.security.LoginEvent;
import org.duguo.xdir.spi.security.User;
import org.duguo.xdir.security.api.authentication.LoginManager;
import org.duguo.xdir.security.api.codec.StringEncoder;


public class LoginManagerImpl implements LoginManager {
    
    
    private static final Logger logger = LoggerFactory.getLogger( LoginManagerImpl.class );
    
    private String userSessionKey = "u";
    private String userInfoCookieKey = "u";
    private String userInfoCookiePath = "/";
    private String rememberMeCookieKey = "rm";
    private StringEncoder rememberMeEncoder;
	private int rememberMexpireInDays=7;
    
    private AccountService accountService;

    public User getUserFromSession( HttpServletRequest request )
    {        
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object userObj = session.getAttribute(userSessionKey);
            if (userObj != null) {
                User user=(User) userObj;
                if(logger.isDebugEnabled())
                    logger.debug("loaded user [{}] from session",user.getUserId());
                return user;
            }
        }
        return null;
    }
    
    public void login(LoginEvent loginEvent,HttpServletResponse response) {
        accountService.onLogin(loginEvent);
        
    	loginEvent.getRequest().getSession(true).setAttribute(userSessionKey, loginEvent.getUser());
    	
        Cookie userInfoCookie=new Cookie(userInfoCookieKey,loginEvent.getUser().getDisplayName());
        userInfoCookie.setPath(userInfoCookiePath);
        
        if(loginEvent.isRememberMe()){
        	String rememberMeCookieValue=rememberMeEncoder.encode(loginEvent.getUser().getUserId());
        	Cookie rememberMeCookie=new Cookie(rememberMeCookieKey,rememberMeCookieValue);
        	rememberMeCookie.setPath(userInfoCookiePath);
            
        	int expireSeconds=rememberMexpireInDays*24*60*60;
        	rememberMeCookie.setMaxAge(expireSeconds);
            response.addCookie(rememberMeCookie);
            
        	userInfoCookie.setMaxAge(expireSeconds);
        }else{
        	userInfoCookie.setMaxAge(-1);
        }

        response.addCookie(userInfoCookie);
        
        if(logger.isDebugEnabled())
            logger.debug("stored user [{}] into session and cookie",loginEvent.getUser().getUserId());
    }

	public void logout(HttpServletRequest request,HttpServletResponse response) {
		Cookie userInfoCookie=new Cookie(userInfoCookieKey,"");
		userInfoCookie.setMaxAge(0);
		userInfoCookie.setPath(userInfoCookiePath);
		response.addCookie(userInfoCookie);
		
		Cookie rememberMeCookie=new Cookie(rememberMeCookieKey,"");
		rememberMeCookie.setMaxAge(0);
		rememberMeCookie.setPath(userInfoCookiePath);
		response.addCookie(rememberMeCookie);
		
		HttpSession session = request.getSession(false);
		if (session != null) {
	        if(logger.isDebugEnabled()){
	        	User user=getUserFromSession(request);
	        	if(user!=null){
	        		logger.info("logged out for user id [{}]",user.getUserId());
	        	}
	        }   
			session.invalidate();
	        if(logger.isDebugEnabled())
	            logger.debug("invalidated session");
		}
        if(logger.isDebugEnabled())
            logger.debug("cleared session and cookie for user");
	}

    public String getUserSessionKey()
    {
        return userSessionKey;
    }

    public void setUserSessionKey( String userSessionKey )
    {
        this.userSessionKey = userSessionKey;
    }

    public String getUserInfoCookieKey()
    {
        return userInfoCookieKey;
    }

    public void setUserInfoCookieKey( String userInfoCookieKey )
    {
        this.userInfoCookieKey = userInfoCookieKey;
    }

    public String getUserInfoCookiePath()
    {
        return userInfoCookiePath;
    }

    public void setUserInfoCookiePath( String userInfoCookiePath )
    {
        this.userInfoCookiePath = userInfoCookiePath;
    }

	public AccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}

	public String getRememberMeCookieKey() {
		return rememberMeCookieKey;
	}

	public void setRememberMeCookieKey(String rememberMeCookieKey) {
		this.rememberMeCookieKey = rememberMeCookieKey;
	}

	public StringEncoder getRememberMeEncoder() {
		return rememberMeEncoder;
	}

	public void setRememberMeEncoder(StringEncoder rememberMeEncoder) {
		this.rememberMeEncoder = rememberMeEncoder;
	}

	public int getRememberMexpireInDays() {
		return rememberMexpireInDays;
	}

	public void setRememberMexpireInDays(int rememberMexpireInDays) {
		this.rememberMexpireInDays = rememberMexpireInDays;
	}

}
