package org.duguo.xdir.security.impl.codec;

import java.util.Random;

import org.duguo.xdir.security.api.codec.RadomGenerator;

public class RadomStringGenerator implements RadomGenerator {
	
	private static final char[] charMap;
	static {
		charMap = new char[62];
		int idx = 0;
		for (int n = 48; n < 123; n++) {
			if (n == 58) {
				n = 65;
			} else if (n == 91) {
				n = 97;
			}
			charMap[idx++] = (char) n;
		}
	}

	public String generate(int length) {
		StringBuffer result = new StringBuffer();
		Random random = new Random(System.currentTimeMillis());
		for (int i = 0; i < length; i++) {
			result.append(charMap[random.nextInt(62)]);
		}
		return result.toString();
	}

}
