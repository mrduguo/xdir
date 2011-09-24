package org.duguo.xdir.security.impl.authentication.facebook;

import org.springframework.util.Assert;
import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.security.api.authentication.facebook.FacebookService;
import org.duguo.xdir.security.impl.authentication.AbstractUserInfoRetriver;

public class FacebookUserInfoRetriver extends AbstractUserInfoRetriver{
	
	private FacebookService facebookService;
    private String userInfoFields="last_name,first_name,email,pic_square,username";

	public Json retriveUserInfo(Object... params) {
		String authToken=(String)params[0];
		Json userIdResult=facebookService.signAndRequest(authToken,"facebook.auth.getSession");
		String userId=userIdResult.get("uid").toString();
		Assert.notNull(userId);
		String sessionKey=userIdResult.get("session_key").toString();
		Assert.notNull(sessionKey);
		if(logger.isDebugEnabled())
			logger.debug("facebook user id [{}]",userId);
		
		Json result=facebookService.signAndRequest(authToken,"facebook.users.getInfo","session_key",sessionKey,"uids",userId,"fields",userInfoFields,"call_id",String.valueOf(System.currentTimeMillis()));
		mapToUserInfo(result);
		return result;
	}


	public FacebookService getFacebookService() {
		return facebookService;
	}

	public void setFacebookService(FacebookService facebookService) {
		this.facebookService = facebookService;
	}

	public String getUserInfoFields() {
		return userInfoFields;
	}


	public void setUserInfoFields(String userInfoFields) {
		this.userInfoFields = userInfoFields;
	}

}
