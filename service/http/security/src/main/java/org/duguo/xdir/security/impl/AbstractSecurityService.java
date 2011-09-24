package org.duguo.xdir.security.impl;

import org.duguo.xdir.spi.model.support.AbstractGetAndPut;
import org.duguo.xdir.spi.security.AccountService;
import org.duguo.xdir.security.api.authentication.Authenticator;
import org.duguo.xdir.security.api.authentication.LoginManager;
import org.duguo.xdir.security.api.codec.EncoderService;
import org.duguo.xdir.security.api.codec.RadomGenerator;
import org.duguo.xdir.security.api.url.AccessDeniedHandler;
import org.duguo.xdir.security.api.url.SecureUrlManager;

public abstract class AbstractSecurityService extends AbstractGetAndPut
{
    private SecureUrlManager securityUrlManager;   
    private AccessDeniedHandler accessDeniedHandler; 
    
    private Authenticator authenticator;
    private LoginManager loginManager;
    private AccountService accountService;
    
    private RadomGenerator radomStringGenerator;
    private RadomGenerator radomNumberGenerator;
    private EncoderService encoderService;

    public SecureUrlManager getSecurityUrlManager()
    {
        return securityUrlManager;
    }

    public void setSecurityUrlManager( SecureUrlManager securityUrlManager )
    {
        this.securityUrlManager = securityUrlManager;
    }

    public Authenticator getAuthenticator()
    {
        return authenticator;
    }

    public void setAuthenticator( Authenticator authenticator )
    {
        this.authenticator = authenticator;
    }

    public AccessDeniedHandler getAccessDeniedHandler()
    {
        return accessDeniedHandler;
    }

    public void setAccessDeniedHandler( AccessDeniedHandler accessDeniedHandler )
    {
        this.accessDeniedHandler = accessDeniedHandler;
    }

    public LoginManager getLoginManager()
    {
        return loginManager;
    }

    public void setLoginManager( LoginManager loginManager )
    {
        this.loginManager = loginManager;
    }

	public RadomGenerator getRadomStringGenerator() {
		return radomStringGenerator;
	}

	public void setRadomStringGenerator(RadomGenerator radomStringGenerator) {
		this.radomStringGenerator = radomStringGenerator;
	}

	public RadomGenerator getRadomNumberGenerator() {
		return radomNumberGenerator;
	}

	public void setRadomNumberGenerator(RadomGenerator radomNumberGenerator) {
		this.radomNumberGenerator = radomNumberGenerator;
	}

	public AccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}

	public EncoderService getEncoderService() {
		return encoderService;
	}

	public void setEncoderService(EncoderService encoderService) {
		this.encoderService = encoderService;
	}

}
