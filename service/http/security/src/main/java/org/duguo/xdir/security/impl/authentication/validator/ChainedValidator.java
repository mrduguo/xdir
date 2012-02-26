package org.duguo.xdir.security.impl.authentication.validator;

import org.duguo.xdir.security.api.authentication.LoginValidator;
import org.duguo.xdir.security.impl.authentication.UserImpl;
import org.duguo.xdir.spi.security.LoginEvent;
import org.duguo.xdir.spi.security.Role;


/**
 * This is for request already authorised by proxy server such apache web server
 * 
 * @author duguo
 *
 */
public class ChainedValidator implements LoginValidator{
	
	private LoginValidator[] validators;

	public int validate(LoginEvent loginEvent) {
        int result=LoginEvent.LOGIN_USER_NAME_NOT_FOUND;
        for (LoginValidator validator: validators) {
            result=validator.validate(loginEvent);
            if(result!=LoginEvent.LOGIN_USER_NAME_NOT_FOUND){
                break;
            }
        }
        return result;
	}

    public LoginValidator[] getValidators() {
        return validators;
    }

    public void setValidators(LoginValidator[] validators) {
        this.validators = validators;
    }
}
