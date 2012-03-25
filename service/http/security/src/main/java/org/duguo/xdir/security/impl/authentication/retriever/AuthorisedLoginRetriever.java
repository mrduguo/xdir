package org.duguo.xdir.security.impl.authentication.retriever;

import org.duguo.xdir.security.api.authentication.LoginRetriever;
import org.duguo.xdir.spi.security.LoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;


public class AuthorisedLoginRetriever implements LoginRetriever{
	
	
    private static final Logger logger = LoggerFactory.getLogger( AuthorisedLoginRetriever.class );
    
    private String loginNameAttributeName="loginName";
	
	public void retrieve(LoginEvent loginEvent){
		HttpSession session = loginEvent.getRequest().getSession(false);
		if(session!=null){
			String userName = (String)session.getAttribute(loginNameAttributeName);
			if(userName!=null){
			    if(logger.isDebugEnabled())
	                logger.debug("retrived login name [{}]",userName);
			    loginEvent.setUserName(userName);
			    session.removeAttribute(loginNameAttributeName);
			}			
		}
	}

	public String getLoginNameAttributeName() {
		return loginNameAttributeName;
	}

	public void setLoginNameAttributeName(String loginNameAttributeName) {
		this.loginNameAttributeName = loginNameAttributeName;
	}

}
