package org.duguo.xdir.security.impl.codec.crypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.springframework.util.Assert;
import org.duguo.xdir.security.api.codec.StringEncoder;

public class SecretDecoder implements StringEncoder {
	
   
	private String encryptionScheme;
	private SecretKey cryptKey;
	

	public String encode(String secret) {
		Assert.hasLength(secret);
		try {
			Cipher cipher = Cipher.getInstance(encryptionScheme);
			cipher.init(Cipher.DECRYPT_MODE, cryptKey);
			
			byte[] secrettext = Base64.decodeBase64(secret);
			byte[] ciphertext = cipher.doFinal(secrettext);
			return bytes2String(ciphertext);
		} catch (Exception ex) {
			throw new RuntimeException("failed to decrypt string:" + ex.getMessage(), ex);
		}
	}

	protected String bytes2String(byte[] bytes) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			stringBuilder.append((char) bytes[i]);
		}
		return stringBuilder.toString();
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
