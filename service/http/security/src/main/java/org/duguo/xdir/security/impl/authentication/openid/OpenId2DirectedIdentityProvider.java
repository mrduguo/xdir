package org.duguo.xdir.security.impl.authentication.openid;


import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.util.http.HttpUtil;

public class OpenId2DirectedIdentityProvider extends AbstractOpenidProvider{
    
    private static final Logger logger = LoggerFactory.getLogger( OpenId2DirectedIdentityProvider.class );

	public Json requestLoginUrl(HttpServletRequest request,String returnUrl){
		String activeEndPointUrl=retriveEndPointUrl(request);
		Json result=(Json)getHttpClientService().httpPost(activeEndPointUrl, "colon", getAssociationRequestParams());
		String assocHandle=result.get("assoc_handle").value().stringValue();
		if(assocHandle!=null){
	        String loginUrl = buildLoginUrl(activeEndPointUrl,returnUrl,assocHandle);
	        result.map().set("loginUrl", loginUrl);
	        
	        if(logger.isDebugEnabled())
	        	logger.debug("login url [{}]",loginUrl);
			
	        getTokenStore().store(request, result.get("mac_key").toString());
		}else{
			logger.error("failed to associate with openid provider [{}]",result);
		}
		return result;
	}
	
	public Json retriveUserInfo(HttpServletRequest request){
		verifySign(request);

        Json result = getUserInfoRetriver().retriveUserInfo(request);
		return result;
	}
	

	protected String buildLoginUrl(String activeEndPointUrl,String returnUrl, String assocHandle) {
		StringBuilder loginUrlSB = new StringBuilder(1024);
		loginUrlSB.append(retriveLoginBaseUrl(activeEndPointUrl))
				.append("&openid.return_to=")
				.append(HttpUtil.encodeParam(returnUrl))
				.append("&openid.assoc_handle=")
				.append(assocHandle);
		
		return loginUrlSB.toString();
	}

	protected void verifySign(HttpServletRequest request) {
        String sig = request.getParameter("openid.sig");
		Assert.notNull(sig);
        
        String signed = request.getParameter("openid.signed");
		Assert.notNull(signed);
		
        String[] singedParams = signed.split(",");
        StringBuilder sb = new StringBuilder(1024);
        for (String param : singedParams) {
            sb.append(param).append(':');
            String value = request.getParameter("openid." + param);
            if (value!=null)
                sb.append(value);
            sb.append('\n');
        }

		String tokenSecret = (String)getTokenStore().retrive(request);
        String hmac = getSignService().sign(tokenSecret,sb.toString());
        if(!sig.equals(hmac)){
        	throw new RuntimeException("Verify signature failed.");
        }
	}

}
