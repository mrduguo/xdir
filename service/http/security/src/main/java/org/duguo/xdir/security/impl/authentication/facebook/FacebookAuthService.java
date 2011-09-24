package org.duguo.xdir.security.impl.authentication.facebook;


import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.http.json.JsonUtil;
import org.duguo.xdir.security.api.authentication.facebook.FacebookService;
import org.duguo.xdir.security.api.codec.StringEncoder;
import org.duguo.xdir.security.impl.authentication.AbstractAuthService;
import org.duguo.xdir.util.http.HttpUtil;


/**
 * http://wiki.developers.facebook.com/index.php/Extended_permissions
 * http://wiki.developers.facebook.com/index.php/How_Facebook_Authenticates_Your_Application
 * http://wiki.developers.facebook.com/index.php/Auth.getSession
 * http://wiki.developers.facebook.com/index.php/Users.getInfo
 * @author duguo
 *
 */
public class FacebookAuthService extends AbstractAuthService implements FacebookService{
    
    private static final Logger logger = LoggerFactory.getLogger( FacebookAuthService.class );

    private String loginUrlBase="http://www.facebook.com/login.php?req_perms=email&next=";
    private String apiVersion="1.0";
    private String restServerUrl="http://api.facebook.com/restserver.php";
    
    private String apiKey;
    private String apiSecret;
    private StringEncoder signEncoder;
    
    
    
    
	public Json requestLoginUrl(HttpServletRequest request,String returnUrl){
		String loginUrl=loginUrlBase+returnUrl+"&v="+apiVersion+"&api_key="+apiKey;
		Json result=JsonUtil.newMap("loginUrl", loginUrl);
		if(logger.isDebugEnabled())
			logger.debug("login url [{}]",loginUrl);
		return result;
	}
	
	public Json retriveUserInfo(HttpServletRequest request){
		String authToken=request.getParameter("auth_token");
		Assert.notNull(authToken);
		
		Json result = getUserInfoRetriver().retriveUserInfo(authToken);		
		return result;
	}
	
	public Json signAndRequest(String authToken, String method, String... params) {
		Map<String, String> requestParams=new TreeMap<String, String>();
		requestParams.put("api_key", apiKey);
		requestParams.put("v", apiVersion);
		requestParams.put("auth_token", authToken);
		requestParams.put("method", method);
		requestParams.put("format", "JSON");
		if(params.length>0){
			for(int i=0;i<params.length/2;i++){
				requestParams.put(params[i*2],params[i*2+1]);
			}
		}

		String requestUrl = signRequest(requestParams);
		
		Json result=getHttpClientService().httpGetJson(requestUrl);
		if(logger.isDebugEnabled())
			logger.debug("request result [{}]",result);
		return result;
	}

	public String signRequest(Map<String, String> requestParams) {
		StringBuilder requestUrl=null;
		StringBuilder signData=new StringBuilder();
		for(Map.Entry<String, String> entry:requestParams.entrySet()){
			if(requestUrl==null){
				requestUrl=new StringBuilder();
				requestUrl.append(restServerUrl);
				requestUrl.append("?");
			}else{
				requestUrl.append("&");				
			}
			requestUrl.append(entry.getKey());
			requestUrl.append("=");
			requestUrl.append(HttpUtil.encodeParam(entry.getValue()));

			signData.append(entry.getKey());
			signData.append("=");
			signData.append(entry.getValue());
		}
		signData.append(apiSecret);
		
		String sign=signEncoder.encode(signData.toString());
		requestUrl.append("&sig=");
		requestUrl.append(sign);
		
		if(logger.isDebugEnabled()){
			logger.debug("sign data [{}]",signData);
			logger.debug("request url [{}]",requestUrl);
		}
		return requestUrl.toString();
	}

	public String getLoginUrlBase() {
		return loginUrlBase;
	}

	public void setLoginUrlBase(String loginUrlBase) {
		this.loginUrlBase = loginUrlBase;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getApiSecret() {
		return apiSecret;
	}

	public void setApiSecret(String apiSecret) {
		this.apiSecret = apiSecret;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getRestServerUrl() {
		return restServerUrl;
	}

	public void setRestServerUrl(String restServerUrl) {
		this.restServerUrl = restServerUrl;
	}

	public StringEncoder getSignEncoder() {
		return signEncoder;
	}

	public void setSignEncoder(StringEncoder signEncoder) {
		this.signEncoder = signEncoder;
	}


}
