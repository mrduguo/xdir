package org.duguo.xdir.http.json;


import java.util.Map;

public interface JsonMap extends Json {
	

	public JsonMap map();
	
	public Map<String, Json> get();
	
	public JsonMap add(String key,Object value);
	
	public JsonMap set(String key,Object value);
	
}
