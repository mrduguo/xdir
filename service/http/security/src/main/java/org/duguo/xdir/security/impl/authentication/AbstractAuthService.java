package org.duguo.xdir.security.impl.authentication;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.http.client.HttpClientService;
import org.duguo.xdir.security.api.authentication.AuthService;
import org.duguo.xdir.security.api.authentication.UserInfoRetriver;
import org.duguo.xdir.security.api.codec.SignService;
import org.duguo.xdir.security.api.token.TokenStore;

public abstract class AbstractAuthService implements AuthService{
    
    public static final Logger logger = LoggerFactory.getLogger( AbstractAuthService.class );
    

	private HttpClientService httpClientService;
	private TokenStore tokenStore;

	private SignService signService;
	private UserInfoRetriver userInfoRetriver;

	public HttpClientService getHttpClientService() {
		return httpClientService;
	}

	public void setHttpClientService(HttpClientService httpClientService) {
		this.httpClientService = httpClientService;
	}


	public SignService getSignService() {
		return signService;
	}

	public void setSignService(SignService signService) {
		this.signService = signService;
	}


	public TokenStore getTokenStore() {
		return tokenStore;
	}

	public void setTokenStore(TokenStore tokenStore) {
		this.tokenStore = tokenStore;
	}

	public UserInfoRetriver getUserInfoRetriver() {
		return userInfoRetriver;
	}

	public void setUserInfoRetriver(UserInfoRetriver userInfoRetriver) {
		this.userInfoRetriver = userInfoRetriver;
	}
}
