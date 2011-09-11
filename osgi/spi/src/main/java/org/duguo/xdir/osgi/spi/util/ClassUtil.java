package org.duguo.xdir.osgi.spi.util;

public class ClassUtil {
	
	@SuppressWarnings("unchecked")
	public static <T> T loadRequiredInstanceFromSystemProperty( Class<T> instanceInterface,Class defaultClass, String classPropertyKey ){
		String customClassName=System.getProperty(classPropertyKey);
		Class targetClass;
		if(customClassName!=null){
			try {
				targetClass= Class.forName(customClassName);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("cannot load required class",e);
			}
		}else{
			targetClass=defaultClass;
		}
		
		try {
			return (T) targetClass.newInstance();
		} catch (IllegalAccessException e) {
			throw new RuntimeException("cannot create instance for class: "+targetClass.getName(),e);
		} catch (InstantiationException e) {
			throw new RuntimeException("cannot create instance for class: "+targetClass.getName(),e);
		}
	}
}
