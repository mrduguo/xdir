package org.duguo.xdir.security.impl.authentication.retriever;

import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.spi.security.LoginEvent;
import org.duguo.xdir.security.api.authentication.LoginRetriever;
import org.duguo.xdir.util.codec.Base64;


public class BasicAuthUserNamePasswordRetriever implements LoginRetriever{
	
	
    private static final Logger logger = LoggerFactory.getLogger( BasicAuthUserNamePasswordRetriever.class );
    
	
	public void retrieve(LoginEvent loginEvent){
		String authHeader = loginEvent.getRequest().getHeader("Authorization");
		if (authHeader != null) {
			StringTokenizer st = new StringTokenizer(authHeader);
			if (st.hasMoreTokens()) {
				String basic = st.nextToken();
				if (basic.equalsIgnoreCase("Basic")) {
					String userPass = new String(new Base64.Decoder().decode(st.nextToken()));
					int p = userPass.indexOf(":");
					if (p != -1) {
						String userName = userPass.substring(0, p);
						String password = userPass.substring(p + 1);
						if(userName!=null && password!=null){
						    if(logger.isDebugEnabled())
                                logger.debug("retrived userName [{}]",userName);
						    loginEvent.setUserName(userName);
						    loginEvent.setPassword(password);
							return;
						}
					}
				}else{
					logger.warn("Unknown Authorization token [{}]",authHeader);
				}
			}
		}
	}
}
