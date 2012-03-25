package org.duguo.xdir.security.impl.codec;

import org.apache.commons.codec.binary.Hex;
import org.duguo.xdir.security.api.codec.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestStringEncoder implements StringEncoder {
	
    private static final Logger logger = LoggerFactory.getLogger( DigestStringEncoder.class );

	/**
	 * support digest such as:
	 * MD5
	 * SHA (SHA-1)
	 * SHA-256
	 * SHA-384
	 * SHA-512
	 */
	private String digestAlgorithm;
	
	/**
	 * Default to 1, you may set a big number force to a slow action to get better security.
	 * 
	 * It taken about 1 second for loop 2,000,000 on my laptop
	 */
	private int digestLoop=1;

	public String encode(String inputStr) {
		MessageDigest messageDigest = getDigest(digestAlgorithm);
		byte[] digestBytes=inputStr.getBytes();
		if(digestLoop>1){
			long spendTime=System.currentTimeMillis();
			for(int i=0;i<digestLoop;i++){
				digestBytes = messageDigest.digest(digestBytes);
			}
	        if(logger.isDebugEnabled()){
				spendTime=System.currentTimeMillis()-spendTime;
	        	logger.debug("generated digest in [{}] milliseconds",spendTime);
	        }
	            
		}else{
			digestBytes = messageDigest.digest(digestBytes);
		}
		
		String digest=Hex.encodeHexString(digestBytes);
        if(logger.isDebugEnabled())
            logger.debug("encoded digest [{}]",digest);
        return digest;
	}

    private MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
	
	public String getDigestAlgorithm() {
		return digestAlgorithm;
	}

	public void setDigestAlgorithm(String digestAlgorithm) {
		this.digestAlgorithm = digestAlgorithm;
	}

	public int getDigestLoop() {
		return digestLoop;
	}

	public void setDigestLoop(int digestLoop) {
		this.digestLoop = digestLoop;
	}

}
