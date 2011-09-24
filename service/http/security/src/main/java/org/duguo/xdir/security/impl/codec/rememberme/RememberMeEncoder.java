package org.duguo.xdir.security.impl.codec.rememberme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RememberMeEncoder extends AbstractRememberMe {

	private static final Logger logger = LoggerFactory.getLogger( RememberMeEncoder.class );

	private int expireInDays=7;

	protected long calculateExpireTimestamp() {
		long expireTimestamp=System.currentTimeMillis()+(expireInDays*24*60*60*1000);
		return expireTimestamp;
	}

	public String encode(String userId) {
		long expireTimestamp=calculateExpireTimestamp();
		String plainCookie=expireTimestamp+":"+userId;
		String encodedCookie=getSecretCodec().encode(plainCookie);
		if(logger.isDebugEnabled())
			logger.debug("encrypted remember me cookie for [{}]",userId);
		return encodedCookie;
	}

	public int getExpireInDays() {
		return expireInDays;
	}

	public void setExpireInDays(int expireInDays) {
		this.expireInDays = expireInDays;
	}

}