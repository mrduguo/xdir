package org.duguo.xdir.core.internal.account;

import org.duguo.xdir.spi.security.User;


public class UserImpl implements User{
	
	private String userId;
	private String displayName;
	private int role;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDisplayName() {
		if(displayName==null){
			return userId;
		}
		return displayName;
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

}
