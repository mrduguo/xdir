package org.duguo.xdir.security.impl.authentication.oauth.signpost;

import javax.servlet.http.HttpServletRequest;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;

import org.apache.http.client.methods.HttpUriRequest;
import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.http.json.JsonUtil;
import org.duguo.xdir.security.impl.authentication.oauth.AbstractOAuthService;



public abstract class AbstractSignpostOAuthService extends AbstractOAuthService{
	
	protected abstract OAuthConsumer retriveOauthConsumer();
	protected abstract OAuthProvider retriveOauthProvider();
	
	public Json requestLoginUrl(HttpServletRequest request, String returnUrl) {
		if(logger.isDebugEnabled()){
			System.setProperty("debug","true");
		}
    	OAuthConsumer consumer = retriveOauthConsumer();
    	OAuthProvider oauthProvider =retriveOauthProvider();
    	try {
			String loginUrl = oauthProvider.retrieveRequestToken(consumer,returnUrl);
	        if(logger.isDebugEnabled())
	        	logger.debug("login url [{}] for token [{}]",loginUrl,consumer.getToken());
	        
	        Json result=JsonUtil.newMap("loginUrl",loginUrl);
	        getTokenStore().store(request, consumer.getTokenSecret());
			return result;
		} catch (Exception ex) {
			throw new RuntimeException("Failed to retrive request token",ex);
		}
	}
	
	public Json retriveUserInfo(HttpServletRequest request) {
    	OAuthConsumer consumer = retriveOauthConsumer();
    	OAuthProvider oauthProvider =retriveOauthProvider();
    	
    	String oauthToken=request.getParameter("oauth_token");
        String oauth_verifier=request.getParameter("oauth_verifier");
        String tokenSecret=(String)getTokenStore().retrive(request);
        
        consumer.setTokenWithSecret(oauthToken, tokenSecret);
        try {
        	oauthProvider.retrieveAccessToken(consumer, oauth_verifier);
			oauthToken=consumer.getToken();
			tokenSecret=consumer.getTokenSecret();
	        if(logger.isDebugEnabled())
	        	logger.debug("retrived access token [{}]",oauthToken);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to retrive access token",ex);
		}

        Json result=requestUserInfo(oauthToken,tokenSecret);
        return result;
	}

	@Override
	public void signRequest(String oautToken, String tokenSecret, HttpUriRequest httpUriRequest) {
        try {
        	OAuthConsumer consumer = retriveOauthConsumer();
            consumer.setTokenWithSecret(oautToken, tokenSecret);
			consumer.sign(httpUriRequest);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to sign request",ex);
		}
	}



}
