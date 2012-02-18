package org.duguo.xdir.security.impl.authentication.oauth;

import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.http.json.JsonUtil;
import org.duguo.xdir.security.api.token.TokenStore;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


/**
 * Default signpost oauth service implementation to create consumer by create bean with scope="prototype" and load via spring bean factory.
 * 
 * @author duguo
 *
 */
public class ScribeOAuthService{

    private static final Logger logger = LoggerFactory.getLogger(ScribeOAuthService.class);
	
	private TokenStore tokenStore;

    public Json retrieveAuthorizeUrl(HttpServletRequest request, Map<String,String> oauthParams) {
        try {
            OAuthService oauthService = createOauthService(oauthParams);
            Token requestToken=null;
            if(!oauthService.getVersion().equals("2.0")){
                requestToken = oauthService.getRequestToken();
                if(logger.isDebugEnabled()) logger.debug("requestToken: {}",requestToken.getToken());
            }
            String authorizeUrl = oauthService.getAuthorizationUrl(requestToken);
            if(logger.isDebugEnabled()) logger.debug("authorizeUrl: {}",authorizeUrl);
            Json result= JsonUtil.newMap("authorizeUrl", authorizeUrl);

            if(requestToken!=null){
                tokenStore.store(request, requestToken.getSecret());
                if(logger.isDebugEnabled()) logger.debug("requestToken secret: {}",requestToken.getSecret());
            }
            return result;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to retrieve authorize url",ex);
        }
    }


    public Json performRequest(HttpServletRequest request, Map<String,String> oauthParams) {
        try {
            OAuthService oauthService = createOauthService(oauthParams);
            Token accessToken=null;
            if(oauthService.getVersion().equals("2.0")){
                String code=request.getParameter("code");
                if(logger.isDebugEnabled()) logger.debug("code: {}",code);
                accessToken = oauthService.getAccessToken(null, new Verifier(code));
            }else{
                String oauthToken=request.getParameter("oauth_token");
                String oauth_verifier=request.getParameter("oauth_verifier");
                String tokenSecret=(String)tokenStore.retrive(request);
                Token requestToken=new Token(oauthToken,tokenSecret);
                accessToken = oauthService.getAccessToken(requestToken, new Verifier(oauth_verifier));
            }
            if(logger.isDebugEnabled()) logger.debug("accessToken : {}",accessToken.getToken());

            OAuthRequest oauthRequest = new OAuthRequest(Verb.GET, oauthParams.get("_oauth_get_request_url"));
            if(oauthParams.containsKey("_oauth_get_request_url")){
                oauthRequest = new OAuthRequest(Verb.GET, oauthParams.get("_oauth_get_request_url"));
                if(logger.isDebugEnabled()) logger.debug("sending get request : {}",oauthParams.get("_oauth_get_request_url"));
            }else if(oauthParams.containsKey("_oauth_post_request_url")){
                oauthRequest = new OAuthRequest(Verb.POST, oauthParams.get("_oauth_post_request_url"));
                if(logger.isDebugEnabled()) logger.debug("sending post request : {}",oauthParams.get("_oauth_post_request_url"));
            }else{
                throw new RuntimeException("No supported service url");
            }
            oauthService.signRequest(accessToken, oauthRequest);
            Response response = oauthRequest.send();
            if(logger.isDebugEnabled()) logger.debug("response code {} and body {}",response.getCode(),response.getBody());
            if(response.getCode()== HttpServletResponse.SC_OK){
                Json result=JsonUtil.textToJson(response.getBody());
                return result;
            }else{
                throw new RuntimeException("Failed to retrieve user info with status code "+response.getCode());
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to send get request",ex);
        }
    }

    private OAuthService createOauthService(Map<String, String> oauthParams) throws ClassNotFoundException {
        ServiceBuilder serviceBuilder = new ServiceBuilder()
                .provider((Class<Api>) Class.forName(oauthParams.get("_oauth_provider")))
                .apiKey(oauthParams.get("_oauth_api_key"))
                .apiSecret(oauthParams.get("_oauth_api_secret"));
        if(oauthParams.containsKey("_oauth_scope")){
            serviceBuilder.scope(oauthParams.get("_oauth_scope"));
            if(logger.isDebugEnabled()) logger.debug("_oauth_scope: {}",oauthParams.get("_oauth_scope"));
        }
        if(oauthParams.containsKey("_oauth_callback_url")){
            serviceBuilder.callback(oauthParams.get("_oauth_callback_url"));
            if(logger.isDebugEnabled()) logger.debug("_oauth_callback_url: {}",oauthParams.get("_oauth_callback_url"));
        }
        if(logger.isDebugEnabled()) serviceBuilder.debug();
        return serviceBuilder.build();
    }

    public TokenStore getTokenStore() {
        return tokenStore;
    }

    public void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }
}
