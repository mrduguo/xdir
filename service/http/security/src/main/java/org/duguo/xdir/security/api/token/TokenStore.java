package org.duguo.xdir.security.api.token;

import javax.servlet.http.HttpServletRequest;

public interface TokenStore {

	public void store(HttpServletRequest request, Object authToken);

	public Object retrive(HttpServletRequest request);

}
