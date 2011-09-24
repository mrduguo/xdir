package org.duguo.xdir.security.impl.authentication.oauth;

import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.security.api.authentication.oauth.OauthService;
import org.duguo.xdir.security.impl.authentication.AbstractUserInfoRetriver;

public class OauthUserInfoRetriver extends AbstractUserInfoRetriver{
	
	private OauthService oauthService;
	private String userInfoUrl;

	public Json retriveUserInfo(Object... params) {
		Json result=oauthService.signAndGet((String)params[0], (String)params[1], userInfoUrl);
		mapToUserInfo(result);
		return result;
	}

	public String getUserInfoUrl() {
		return userInfoUrl;
	}

	public void setUserInfoUrl(String userInfoUrl) {
		this.userInfoUrl = userInfoUrl;
	}

	public OauthService getOauthService() {
		return oauthService;
	}

	public void setOauthService(OauthService oauthService) {
		this.oauthService = oauthService;
	}

}
