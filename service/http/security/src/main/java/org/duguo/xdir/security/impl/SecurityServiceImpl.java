package org.duguo.xdir.security.impl;


import org.duguo.xdir.security.impl.authentication.LoginEventImpl;
import org.duguo.xdir.spi.model.Model;
import org.duguo.xdir.spi.security.LoginEvent;
import org.duguo.xdir.spi.security.Role;
import org.duguo.xdir.spi.security.SecurityService;
import org.duguo.xdir.spi.security.User;
import org.duguo.xdir.spi.service.DynamicService;

public class SecurityServiceImpl extends AbstractSecurityService implements SecurityService,DynamicService
{
    
    
    public boolean requireSecureUrl( Model model ) throws Exception
    {
        return getSecurityUrlManager().requireSecureUrl( model );
    }

    public void accessDenied( Model model, String loginUrl )
    {
        getAccessDeniedHandler().accessDenied(model, loginUrl);
    }


    public boolean isAuthenticated( Model model )
    {
        return getUser(model)!=null;
    }

    public User login( Model model )
    {
    	LoginEvent loginEvent=new LoginEventImpl();
    	loginEvent.setRequest(model.getRequest());
        int result=getAuthenticator().authenticate(loginEvent);
        if (result==LoginEvent.LOGIN_SUCCESS) {
            getLoginManager().login(loginEvent,model.getResponse());
            return loginEvent.getUser();
        }else{
        	return null;
        }
    }

    public void logout( Model model )
    {
        getLoginManager().logout(model.getRequest(),model.getResponse());
    }

    public String getUserId( Model model )
    {
        User user=getUser(model);
        if(user!=null){
            return user.getUserId();
        }else{
            return null;
        }
    }

    public User getUser( Model model )
    {
        User user = getLoginManager().getUserFromSession( model.getRequest() );
        if (user== null) {
            user=login(model);
        }
        if(user!=null){
            model.setUserId( user.getUserId() );
        }
        return user;
    }

    public boolean hasRole( Model model, int role)
    {
    	User user=getUser(model);
    	if(user!=null){
    		return user.getRole()>=role;
    	}else{
    		return false;
    	}
    }

	public boolean inGroup(Model model, String groupPath, Object... aclInfo) {
		return hasGroupRole(model, groupPath,Role.USER, aclInfo);
	}

	public boolean hasGroupRole(Model model, String groupPath, int role, Object... aclInfo) {
		return groupRole(model, groupPath, aclInfo)>=role;
	}

	public int groupRole(Model model, String groupPath, Object... aclInfo) {
    	User user=getUser(model);
    	if(user!=null){
    		return getAccountService().groupRole(user.getUserId(), groupPath, aclInfo);
    	}else{
    		return 0;
    	}
	}
    


    public String radomNumber( int length )
    {
        return getRadomNumberGenerator().generate(length);
    }

    public String radomString( int length )
    {
        return getRadomStringGenerator().generate(length);
    }

    public String encode(String provider, String secret )
    {
        return getEncoderService().encode(provider,secret);
    }

    public String decrypt( String secret )
    {
        return encode("decrypt",secret);
    }

    public String digest( String secret )
    {
        return encode("digest",secret);
    }

    public String encrypt( String secret )
    {
        return encode("encrypt",secret);
    }

    @Override
    public Object getServiceInstance() {
        return this;
    }

    @Override
    public String getServiceName() {
        return System.getProperty("xdir.service.security.service.name","security");
    }
}
