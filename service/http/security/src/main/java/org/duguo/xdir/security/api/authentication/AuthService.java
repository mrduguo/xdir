package org.duguo.xdir.security.api.authentication;

import org.duguo.xdir.http.json.Json;

import javax.servlet.http.HttpServletRequest;

public interface AuthService {

	public Json requestLoginUrl(HttpServletRequest request, String returnUrl);

	public Json retriveUserInfo(HttpServletRequest request);

}
