package org.duguo.xdir.security.impl.codec.crypt;

import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.duguo.xdir.security.api.codec.StringEncoder;

public class KeyFactoryBean implements FactoryBean<SecretKey>,InitializingBean {
	
	public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
	public static final String DES_ENCRYPTION_SCHEME = "DES";
	
	private String encryptionScheme;
	private String encryptionKey;
	
	private SecretKey key;

	public void afterPropertiesSet() throws Exception {
		Assert.hasLength(encryptionScheme);
		Assert.hasLength(encryptionKey);
		Assert.isTrue(encryptionKey.length()>=24, "encryption key was shorter than 24 chars");
		
		try {
			KeySpec keySpec;
			byte[] keyAsBytes = encryptionKey.getBytes(StringEncoder.UNICODE_FORMAT);
			if (encryptionScheme.equals(DESEDE_ENCRYPTION_SCHEME)) {
				keySpec = new DESedeKeySpec(keyAsBytes);
			} else if (encryptionScheme.equals(DES_ENCRYPTION_SCHEME)) {
				keySpec = new DESKeySpec(keyAsBytes);
			} else {
				throw new IllegalArgumentException("Encryption scheme not supported: " + encryptionScheme);
			}

			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(encryptionScheme);
			key = keyFactory.generateSecret(keySpec);
			encryptionKey=null;
		} catch (Exception e) {
			throw new RuntimeException("failed to create encrypter key: " + e.getMessage(), e);
		}
	}
	
	public SecretKey getObject() throws Exception {
		return key;
	}

	public Class<? extends SecretKey> getObjectType() {
		return SecretKey.class;
	}

	public boolean isSingleton() {
		return true;
	}



	public String getEncryptionScheme() {
		return encryptionScheme;
	}


	public void setEncryptionScheme(String encryptionScheme) {
		this.encryptionScheme = encryptionScheme;
	}

	public String getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}
	
}
