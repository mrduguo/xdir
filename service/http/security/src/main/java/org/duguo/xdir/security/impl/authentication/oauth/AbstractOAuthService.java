package org.duguo.xdir.security.impl.authentication.oauth;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.duguo.xdir.http.client.HttpClientUtil;
import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.security.api.authentication.oauth.OauthService;
import org.duguo.xdir.security.impl.authentication.AbstractAuthService;



public abstract class AbstractOAuthService extends AbstractAuthService implements OauthService{
	
	private String apiResponseFormat;
	
	public abstract void signRequest(String oautToken, String tokenSecret,HttpUriRequest httpUriRequest);

	public Json requestUserInfo(String oautToken, String tokenSecret) {
        Json result = getUserInfoRetriver().retriveUserInfo(oautToken, tokenSecret);
        
		result.map().set("oauth_token",oautToken);
		result.map().set("token_secret",tokenSecret);
		
        return result;
	}

	public Json signAndGet(String oautToken, String tokenSecret,String requestUrl) {
		HttpGet httpUriRequest = new HttpGet(requestUrl);
		return signAndRequest(oautToken, tokenSecret, httpUriRequest);
	}

	public Json signAndPost(String oautToken, String tokenSecret,String requestUrl,Object... params) {
		HttpPost httpUriRequest = new HttpPost(requestUrl);
		HttpClientUtil.addPostParameters(httpUriRequest, params);		
		return signAndRequest(oautToken, tokenSecret, httpUriRequest);
	}

	public Json signAndRequest(String oautToken, String tokenSecret, HttpUriRequest httpUriRequest) {
		HttpClient httpClient = getHttpClientService().createClient();
		ResponseHandler<Object> responseHandler = getHttpClientService().retriveResponseHandler(apiResponseFormat);
		signRequest(oautToken,tokenSecret,httpUriRequest);
        Json result=(Json)getHttpClientService().httpRequest(httpUriRequest, responseHandler, httpClient);
		return result;
	}

	public String getApiResponseFormat() {
		return apiResponseFormat;
	}

	public void setApiResponseFormat(String apiResponseFormat) {
		this.apiResponseFormat = apiResponseFormat;
	}




}
