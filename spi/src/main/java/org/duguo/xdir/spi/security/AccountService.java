package org.duguo.xdir.spi.security;

public interface AccountService
{
	public int login(LoginEvent loginEvent);
	
	public void onLogin(LoginEvent loginEvent);
	
	public int groupRole(String userId,String groupPath, Object... aclInfo);
	
}
