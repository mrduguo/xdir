package org.duguo.xdir.security.api.url;

import org.duguo.xdir.spi.model.Model;


public interface AccessDeniedHandler {
	void accessDenied(Model model, String loginUrl);
}
