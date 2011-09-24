package org.duguo.xdir.security.impl.authentication.openid;

import javax.servlet.http.HttpServletRequest;

import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.http.json.impl.parser.RequestParametersParser;
import org.duguo.xdir.security.impl.authentication.AbstractUserInfoRetriver;

public class OpenIdUserInfoRetriver extends AbstractUserInfoRetriver{
	
	public Json retriveUserInfo(Object... params) {
		Json result=RequestParametersParser.parseRequest((HttpServletRequest)params[0]);		
		mapToUserInfo(result);
		return result;
	}
	
}
