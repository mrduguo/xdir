package org.duguo.xdir.security.impl.authentication.retriever;

import org.duguo.xdir.security.api.authentication.LoginRetriever;
import org.duguo.xdir.spi.security.LoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HeaderBasedLoginRetriever implements LoginRetriever{
	
	
    private static final Logger logger = LoggerFactory.getLogger( HeaderBasedLoginRetriever.class );
    
    private String headerName;
	
	public void retrieve(LoginEvent loginEvent){
		String userName = loginEvent.getRequest().getHeader(headerName);
		loginEvent.setUserName(userName);
		if(userName!=null && logger.isDebugEnabled())
			logger.debug("login header [{}]",userName);
	}

	public String getHeaderName() {
		return headerName;
	}

	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}
}
