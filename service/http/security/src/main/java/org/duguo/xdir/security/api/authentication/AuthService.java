package org.duguo.xdir.security.api.authentication;

import javax.servlet.http.HttpServletRequest;

import org.duguo.xdir.http.json.Json;

public interface AuthService {

	public Json requestLoginUrl(HttpServletRequest request, String returnUrl);

	public Json retriveUserInfo(HttpServletRequest request);

}
