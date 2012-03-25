package org.duguo.xdir.security.impl.authentication.normalizer;

import org.duguo.xdir.security.api.authentication.LoginNormalizer;
import org.duguo.xdir.spi.security.LoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class UserNamePasswordNormalizer implements LoginNormalizer{
	
    
    private static final Logger logger = LoggerFactory.getLogger( UserNamePasswordNormalizer.class );
    
	private String userNameStripout=" |-|\\(|\\)";
	
	public void normalize(LoginEvent loginEvent){
        String normalizedUsername=loginEvent.getUserName().replaceAll(userNameStripout, "");
		if(logger.isDebugEnabled())
            logger.debug("normalized userName [{}] as [{}]",loginEvent.getUserName(),normalizedUsername);
		if(normalizedUsername.length()==0){
			loginEvent.setUserName(null);
		}else{
			loginEvent.setUserName(normalizedUsername);
			if(loginEvent.getPassword()!=null){
				loginEvent.setPassword(loginEvent.getPassword().trim());
			}			
		}
	}

	public String getUserNameStripout() {
		return userNameStripout;
	}

	public void setUserNameStripout(String userNameStripout) {
		this.userNameStripout = userNameStripout;
	}


}
