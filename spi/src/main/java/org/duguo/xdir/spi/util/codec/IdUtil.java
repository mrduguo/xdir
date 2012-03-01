package org.duguo.xdir.spi.util.codec;


public class IdUtil {
	
	public static String PATH_ID_CHARS="[-@\\.:/\\\\\\]\\[]";
	public static String JAVA_ID_CHARS="[^A-Za-z0-9]";
	
	public static String normalizePathId(String rawId){
		String id=rawId.replaceAll(PATH_ID_CHARS, "_");
		return id;
	}
	
	public static String normalizeJavaId(String rawId){
		String id=rawId.replaceAll(JAVA_ID_CHARS, "_");
		return id;
	}
}
