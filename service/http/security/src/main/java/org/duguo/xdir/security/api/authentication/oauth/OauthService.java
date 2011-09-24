package org.duguo.xdir.security.api.authentication.oauth;

import org.apache.http.client.methods.HttpUriRequest;
import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.security.api.authentication.AuthService;

public interface OauthService extends AuthService{
	
	public void signRequest(String oautToken, String tokenSecret,HttpUriRequest httpUriRequest);
	
	public Json signAndGet(String oautToken, String tokenSecret,String requestUrl);
	public Json signAndPost(String oautToken, String tokenSecret,String requestUrl,Object... params);
	public Json signAndRequest(String oautToken, String tokenSecret, HttpUriRequest httpUriRequest);

}
