package org.duguo.xdir.security.impl.authentication.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.spi.security.AccountService;
import org.duguo.xdir.spi.security.LoginEvent;
import org.duguo.xdir.security.api.authentication.LoginValidator;
import org.duguo.xdir.security.api.codec.PasswordEncoder;

public class AccountServiceUserValidator implements LoginValidator{
    private static final Logger logger = LoggerFactory.getLogger( AccountServiceUserValidator.class );

	private PasswordEncoder passwordEncoder;
	private AccountService accountService;
	

	public int validate(LoginEvent loginEvent) {
		String rawPassword=loginEvent.getPassword();
		if(rawPassword!=null){
			String encodedPassword=passwordEncoder.encode(loginEvent);
			loginEvent.setPassword(encodedPassword);
		}

		int result=accountService.login(loginEvent);
		if(result==LoginEvent.LOGIN_SUCCESS){
			loginEvent.setName(getClass().getSimpleName());
			if(logger.isDebugEnabled())
				logger.debug("logged in success for [{}]",loginEvent.getUserName());
		}else{
			loginEvent.setPassword(rawPassword);
		}
		return result;
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public AccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
}
