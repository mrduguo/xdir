package org.duguo.xdir.security.impl.url;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.spi.model.Model;
import org.duguo.xdir.security.api.url.AccessDeniedHandler;


public class AccessDeniedHandlerImpl implements AccessDeniedHandler{
	
	private static final Logger logger = LoggerFactory.getLogger( AccessDeniedHandlerImpl.class );
    
	private String authRealm="XDir Login";
	
	public void accessDenied(Model model, String loginUrl){
		if (loginUrl != null && loginUrl.length()>0) {
			try {
				model.getResponse().sendRedirect(loginUrl);
			} catch (IOException e) {
				throw new RuntimeException("Redirect " + loginUrl + " failed " + e.getMessage(), e);
			}
		} else {
			String authHeader=buildAuthHeader(model);
			if(logger.isDebugEnabled())
				logger.debug("setup auth header [{}]",authHeader);
			model.getResponse().setHeader("WWW-Authenticate", authHeader);
			model.getResponse().setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	protected String buildAuthHeader(Model model) {
		return "Basic realm=\""+authRealm+"\"";
	}

	public String getAuthRealm() {
		return authRealm;
	}

	public void setAuthRealm(String authRealm) {
		this.authRealm = authRealm;
	}
}
