package org.duguo.xdir.security.impl.codec.crypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.springframework.util.Assert;
import org.duguo.xdir.security.api.codec.StringEncoder;

public class SecretEncoder implements StringEncoder {
	
	private String encryptionScheme;
	private SecretKey cryptKey;
	

	public String encode(String secret) {
		Assert.hasLength(secret);
		try {
			Cipher cipher = Cipher.getInstance(encryptionScheme);
			cipher.init(Cipher.ENCRYPT_MODE, cryptKey);
			byte[] cleartext = secret.getBytes(UNICODE_FORMAT);
			byte[] ciphertext = cipher.doFinal(cleartext);

			return new String(Base64.encodeBase64(ciphertext));
		} catch (Exception ex) {
			throw new RuntimeException("failed to encrypt string:" + ex.getMessage(), ex);
		}
	}


	public String getEncryptionScheme() {
		return encryptionScheme;
	}


	public void setEncryptionScheme(String encryptionScheme) {
		this.encryptionScheme = encryptionScheme;
	}


	public SecretKey getCryptKey() {
		return cryptKey;
	}


	public void setCryptKey(SecretKey cryptKey) {
		this.cryptKey = cryptKey;
	}


}
