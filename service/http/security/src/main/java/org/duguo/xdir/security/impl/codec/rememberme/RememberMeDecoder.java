package org.duguo.xdir.security.impl.codec.rememberme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RememberMeDecoder extends AbstractRememberMe {

	private static final Logger logger = LoggerFactory.getLogger( RememberMeDecoder.class );

	public String encode(String secretCookie) {
		String plainCookie=getSecretCodec().encode(secretCookie);
		if(plainCookie!=null){
			String[] timestampAndUserId=plainCookie.split(":",2);
			long expireTimestamp=Long.parseLong(timestampAndUserId[0]);
			if(expireTimestamp>System.currentTimeMillis()){
				if(logger.isDebugEnabled())
					logger.debug("decrypted remember me cookie for [{}]",timestampAndUserId[1]);
				return timestampAndUserId[1];
			}
		}
		return null;
	}

}
