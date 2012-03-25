package org.duguo.xdir.security.impl.authentication;

import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.http.json.JsonMap;
import org.duguo.xdir.http.json.JsonUtil;
import org.duguo.xdir.security.api.authentication.UserInfoRetriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class AbstractUserInfoRetriver implements UserInfoRetriver{

    public static final Logger logger = LoggerFactory.getLogger( AbstractUserInfoRetriver.class );
    
	private Map<String,String> userInfoMap;
	
	protected void mapToUserInfo(Json result) {
		JsonMap userInfo=JsonUtil.newMap();

		for(Map.Entry<String, String> entry:getUserInfoMap().entrySet()){
			Json value=JsonUtil.retrive(result,entry.getValue());
			userInfo.set(entry.getKey(), value);
		    if(logger.isDebugEnabled())
		    	logger.debug("map user info [{}:{}]",entry.getKey(), value);
		}
		populateFullName(userInfo);
		result.map().set("user", userInfo);

		if(logger.isDebugEnabled())
			logger.debug("user info result [{}]",result);
	}

	protected void populateFullName(JsonMap userInfo) {
		if(userInfo.get("fullName").isNull()){
			String fullName=null;
			if(!userInfo.get("firstName").isNull()){
				fullName=userInfo.get("firstName").toString();
			}
			if(!userInfo.get("lastName").isNull()){
				if(fullName==null){
					fullName=userInfo.get("lastName").toString();
				}else{
					fullName=fullName +" "+ userInfo.get("lastName").toString();
				}
			}
			if(fullName!=null){
				userInfo.set("fullName", fullName);
				if(logger.isDebugEnabled())
					logger.debug("constructed fullName [{}] from firstName/lastName",fullName);
			}
		}
	}

	public Map<String, String> getUserInfoMap() {
		return userInfoMap;
	}

	public void setUserInfoMap(Map<String, String> userInfoMap) {
		this.userInfoMap = userInfoMap;
	}
}
