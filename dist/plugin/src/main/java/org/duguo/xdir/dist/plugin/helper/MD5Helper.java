package org.duguo.xdir.dist.plugin.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.security.MessageDigest;

public class MD5Helper {
	
	private static byte[] createChecksum(File fileToCheck) throws Exception {
		InputStream fis = new FileInputStream(fileToCheck);

		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;
		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);
		fis.close();
		return complete.digest();
	}

	public static String generateMD5Checksum(File fileToCheck) throws Exception {
		byte[] b = createChecksum(fileToCheck);
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	public static void storeMD5Checksum(File fileToCheck,File fileToStore) throws Exception {
		String md5Checksum=generateMD5Checksum(fileToCheck);
		FileWriter fstream = new FileWriter(fileToStore);
		BufferedWriter out = new BufferedWriter(fstream);
		try {
			out.write(md5Checksum);
		} finally {
			out.close();
		}
	}

}
