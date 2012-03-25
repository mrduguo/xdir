package org.duguo.xdir.security.impl.authentication.openid;

import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.http.json.JsonUtil;
import org.duguo.xdir.security.impl.authentication.AbstractAuthService;
import org.duguo.xdir.spi.util.http.HttpUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;



public abstract class AbstractOpenidProvider extends AbstractAuthService{

	private String discoveryUrl;
	private String discoveryPath;
	private String endPointUrl;

	private Map<String,String> associationRequestParams;
	
	private Map<String,String> loginUrlBaseParams;
	private Map<String,String> loginUrlRequiredFields;
	private String loginUrlNamespacePrefix;
	private String loginUrlBase;
	private String loginUrlRealm;

	protected String retriveEndPointUrl(HttpServletRequest request){
		if(endPointUrl==null){
			Json result=(Json)getHttpClientService().httpGetXml(discoveryUrl);
			endPointUrl=JsonUtil.retrive(result,discoveryPath).toString();
			if(endPointUrl==null){
				throw new RuntimeException("Failed to discovery end point:\n"+result);
			}else if(logger.isDebugEnabled()){
					logger.debug("discovered end point [{}]",endPointUrl);
			}
		}
		return endPointUrl;
	}
	
	protected String retriveLoginBaseUrl(String activeEndPointUrl){
		if(loginUrlBase==null){
	        StringBuilder baseUrl = new StringBuilder(1024);

	        baseUrl.append(activeEndPointUrl);
	        boolean isFirstParam=false;
	        if(!endPointUrl.contains("?")){
	        	baseUrl.append("?");
	        	isFirstParam=true;
	        }

			if(loginUrlBaseParams!=null){
				for(Map.Entry<String, String> entry:loginUrlBaseParams.entrySet()){
					if(isFirstParam){
						isFirstParam=false;
					}else{
						baseUrl.append("&");
					}
					baseUrl.append(entry.getKey());
					baseUrl.append("=");
					baseUrl.append(HttpUtil.encodeParam(entry.getValue()));
				}
			}

			if(loginUrlRequiredFields!=null){
		        StringBuilder fields=null;
		        for(Map.Entry<String, String> entry:loginUrlRequiredFields.entrySet()){
					if(isFirstParam){
						isFirstParam=false;
					}else{
						baseUrl.append("&");
					}
					baseUrl.append(loginUrlNamespacePrefix);
					baseUrl.append(".type.");
					baseUrl.append(entry.getKey());
					baseUrl.append("=");
					baseUrl.append(HttpUtil.encodeParam(entry.getValue()));
					
		        	if(fields==null){
		        		fields=new StringBuilder();
		        	}else{
		        		fields.append(",");
		        	}
		        	fields.append(entry.getKey());
		        }
				baseUrl.append("&");
				baseUrl.append(loginUrlNamespacePrefix);
				baseUrl.append(".required");
				baseUrl.append("=");
				baseUrl.append(HttpUtil.encodeParam(fields.toString()));
			}
			
	        if(loginUrlRealm!=null){
	        	baseUrl.append("&openid.realm=");
	        	baseUrl.append(HttpUtil.encodeParam(loginUrlRealm));
	        }
        
	        
	        loginUrlBase=baseUrl.toString();
	        

	        if(logger.isDebugEnabled())
	        	logger.debug("login url base [{}]",loginUrlBase);
		}
		return loginUrlBase;
	}



	public String getEndPointUrl() {
		return endPointUrl;
	}

	public void setEndPointUrl(String endPointUrl) {
		this.endPointUrl = endPointUrl;
	}
	

	public String getDiscoveryUrl() {
		return discoveryUrl;
	}

	public void setDiscoveryUrl(String discoveryUrl) {
		this.discoveryUrl = discoveryUrl;
	}


	public String getDiscoveryPath() {
		return discoveryPath;
	}

	public void setDiscoveryPath(String discoveryPath) {
		this.discoveryPath = discoveryPath;
	}

	public Map<String, String> getAssociationRequestParams() {
		return associationRequestParams;
	}

	public void setAssociationRequestParams(
			Map<String, String> associationRequestParams) {
		this.associationRequestParams = associationRequestParams;
	}


	public Map<String, String> getLoginUrlBaseParams() {
		return loginUrlBaseParams;
	}

	public void setLoginUrlBaseParams(Map<String, String> loginUrlBaseParams) {
		this.loginUrlBaseParams = loginUrlBaseParams;
	}

	public Map<String, String> getLoginUrlRequiredFields() {
		return loginUrlRequiredFields;
	}

	public void setLoginUrlRequiredFields(Map<String, String> loginUrlRequiredFields) {
		this.loginUrlRequiredFields = loginUrlRequiredFields;
	}

	public String getLoginUrlNamespacePrefix() {
		return loginUrlNamespacePrefix;
	}

	public void setLoginUrlNamespacePrefix(String loginUrlNamespacePrefix) {
		this.loginUrlNamespacePrefix = loginUrlNamespacePrefix;
	}

	public String getLoginUrlBase() {
		return loginUrlBase;
	}

	public void setLoginUrlBase(String loginUrlBase) {
		this.loginUrlBase = loginUrlBase;
	}

	public String getLoginUrlRealm() {
		return loginUrlRealm;
	}

	public void setLoginUrlRealm(String loginUrlRealm) {
		this.loginUrlRealm = loginUrlRealm;
	}
}
