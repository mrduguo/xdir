package org.duguo.xdir.security.impl.authentication;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.duguo.xdir.spi.security.LoginEvent;
import org.duguo.xdir.spi.security.User;

public class LoginEventImpl implements LoginEvent{

	public String name;
	public String userName;
	public String password;
	public boolean rememberMe;
	public User user;
	public HttpServletRequest request;
	public Map<Object, Object> attributes;
	
	public String getName() {
		return name;
	}
	
	public Object getSource() {
		return request;
	}

	public Map<Object, Object> getAttributes() {
		return attributes;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setAttributes(Map<Object, Object> attributes) {
		this.attributes = attributes;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}
	
}
