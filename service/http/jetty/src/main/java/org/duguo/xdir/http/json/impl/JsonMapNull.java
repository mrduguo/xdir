package org.duguo.xdir.http.json.impl;

import java.util.Map;

import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.http.json.JsonMap;

public class JsonMapNull extends JsonMapImpl{
	
	private static final JsonMapNull NULL_INSTANCE=new JsonMapNull();

	// singleton only
	private JsonMapNull(){
	}
	
	public static JsonMap instance(){
		return NULL_INSTANCE;
	}

	public Map<String, Json> get() {
		return null;
	}

	public Json get(Object key) {
		return JsonNull.instance();
	}

	public boolean isNull() {
		return true;
	}
	
	public String toString(){
		return null;
	}
}
