package org.duguo.xdir.spi.security;

import org.duguo.xdir.spi.event.Event;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface LoginEvent extends Event{

	public static final int LOGIN_SUCCESS=0;
	public static final int LOGIN_USER_NAME_NOT_FOUND=1;
	public static final int LOGIN_WRONG_PASSWORD=2;
	public static final int LOGIN_USER_INACTIVE=3;
	public static final int LOGIN_RESTRICTED=4;
	public static final int LOGIN_EXPIRED=5;
	
	public void setName(String name);
	public void setAttributes(Map<Object, Object> attributes);

	public String getUserName();
	public void setUserName(String userName);
	
	public String getPassword();
	public void setPassword(String password);
	
	public boolean isRememberMe();
	public void setRememberMe(boolean rememberMe);
		
	public User getUser();
	public void setUser(User user);
	
	public HttpServletRequest getRequest();
	public void setRequest(HttpServletRequest request);
	

}
