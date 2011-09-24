package org.duguo.xdir.security.impl.codec.crypt;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.security.api.codec.SignService;

public class DefaultSignService implements SignService {
	  

    private static final Logger logger = LoggerFactory.getLogger( DefaultSignService.class );
    
	/*
	 * HmacSHA1
	 */
    private String algorithm = "HmacSHA1";

	public String sign(String key, String data) {
		
		try{
	        SecretKeySpec signingKey = new SecretKeySpec(Base64.decodeBase64(key), algorithm);
	        Mac mac= Mac.getInstance(algorithm);
	        mac.init(signingKey);

            byte[] rawSign = mac.doFinal(data.getBytes("UTF-8"));
            String sign=new String(Base64.encodeBase64(rawSign));
            if(logger.isDebugEnabled())
            	logger.debug("sign [{}] for data [{}]",sign,data);
            return sign;        
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}


	public String getAlgorithm() {
		return algorithm;
	}


	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}


}
