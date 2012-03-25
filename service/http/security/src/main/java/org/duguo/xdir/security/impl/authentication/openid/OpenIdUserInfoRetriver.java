package org.duguo.xdir.security.impl.authentication.openid;

import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.http.json.impl.parser.RequestParametersParser;
import org.duguo.xdir.security.impl.authentication.AbstractUserInfoRetriver;

import javax.servlet.http.HttpServletRequest;

public class OpenIdUserInfoRetriver extends AbstractUserInfoRetriver{
	
	public Json retriveUserInfo(Object... params) {
		Json result=RequestParametersParser.parseRequest((HttpServletRequest)params[0]);		
		mapToUserInfo(result);
		return result;
	}
	
}
