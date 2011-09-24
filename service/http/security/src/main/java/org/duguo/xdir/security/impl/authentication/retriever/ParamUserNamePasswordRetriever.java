package org.duguo.xdir.security.impl.authentication.retriever;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.spi.security.LoginEvent;
import org.duguo.xdir.security.api.authentication.LoginRetriever;

public class ParamUserNamePasswordRetriever implements LoginRetriever{
	
    
    private static final Logger logger = LoggerFactory.getLogger( ParamUserNamePasswordRetriever.class );
    
	private String userNameField="username";
	private String passwordField="password";
	private String rememberMeField="rememberme";
	
	public void retrieve(LoginEvent loginEvent){
		String userName=loginEvent.getRequest().getParameter(userNameField);
		String password=loginEvent.getRequest().getParameter(passwordField);
		if(userName!=null && password!=null){
		    if(logger.isDebugEnabled())
                logger.debug("retrived userName [{}]",userName);
		    loginEvent.setUserName(userName);
		    loginEvent.setPassword(password);
		    
		    String rememberMe=loginEvent.getRequest().getParameter(rememberMeField);
		    if("true".equals(rememberMe)){
		    	loginEvent.setRememberMe(true);
		    }
		    
		}
	}

    public String getPasswordField()
    {
        return passwordField;
    }

    public void setPasswordField( String passwordField )
    {
        this.passwordField = passwordField;
    }

	public String getUserNameField() {
		return userNameField;
	}

	public void setUserNameField(String userNameField) {
		this.userNameField = userNameField;
	}

	public String getRememberMeField() {
		return rememberMeField;
	}

	public void setRememberMeField(String rememberMeField) {
		this.rememberMeField = rememberMeField;
	}
	
	
}
