package org.duguo.xdir.security.impl.token;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.duguo.xdir.security.api.token.TokenStore;


public class SessionTokenStore implements TokenStore {

	private String storeKey;

	public void store(HttpServletRequest request, Object authToken) {
		HttpSession session = request.getSession(true);
		session.setAttribute(storeKey, authToken);
	}

	public Object retrive(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if(session!=null){
			Object tokenSecret=session.getAttribute(storeKey);
			if(tokenSecret!=null){
				session.removeAttribute(storeKey);
				return tokenSecret;
			}
		}
		throw new RuntimeException("session timeout");
	}

	public String getStoreKey() {
		return storeKey;
	}

	public void setStoreKey(String storeKey) {
		this.storeKey = storeKey;
	}
}
