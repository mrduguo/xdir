package org.duguo.xdir.security.impl.authentication;

import org.duguo.xdir.spi.security.User;


public class UserImpl implements User{
	
	private String userId;
	private String displayName;
	private int role;
	
	private String userName;
	private String password;
	
	private String email;

	public String getUserName() {
		if(userName==null){
			return userId;
		}
		return userName;
	}

	public String getDisplayName() {
		if(displayName==null){
			return userId;
		}
		return displayName;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
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
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

}
