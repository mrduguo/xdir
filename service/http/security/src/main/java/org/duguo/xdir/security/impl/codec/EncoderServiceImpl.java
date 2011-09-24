package org.duguo.xdir.security.impl.codec;

import java.util.Map;

import org.duguo.xdir.security.api.codec.EncoderService;
import org.duguo.xdir.security.api.codec.StringEncoder;

public class EncoderServiceImpl implements EncoderService{
	
	private Map<String,StringEncoder> encoderProviders;
	
	public String encode(String provider,String secret){
		StringEncoder encoder=encoderProviders.get(provider);
		if(encoder!=null){
			return encoder.encode(secret);
		}else{
			throw new RuntimeException("Unsuppored encoder provider ["+provider+"]");
		}
	}

	public Map<String, StringEncoder> getEncoderProviders() {
		return encoderProviders;
	}

	public void setEncoderProviders(Map<String, StringEncoder> encoderProviders) {
		this.encoderProviders = encoderProviders;
	}

}
