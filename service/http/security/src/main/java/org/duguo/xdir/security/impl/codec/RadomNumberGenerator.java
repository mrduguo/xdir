package org.duguo.xdir.security.impl.codec;

import java.util.Random;

import org.duguo.xdir.security.api.codec.RadomGenerator;

public class RadomNumberGenerator implements RadomGenerator {

	public String generate(int length) {
		StringBuffer result = new StringBuffer();
		Random random = new Random(System.currentTimeMillis());
		result.append((random.nextInt(9) + 1));
		if (length > 1) {
			for (int i = 1; i < length; i++) {
				result.append(random.nextInt(10));
			}
		}
		return result.toString();
	}

}
