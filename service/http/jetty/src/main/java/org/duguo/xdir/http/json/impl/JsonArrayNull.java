package org.duguo.xdir.http.json.impl;

import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.http.json.JsonArray;

import java.util.List;

public class JsonArrayNull extends JsonArrayImpl{
	
	private static final JsonArrayNull NULL_INSTANCE=new JsonArrayNull();

	// singleton only
	private JsonArrayNull(){
	}
	
	public static JsonArray instance(){
		return NULL_INSTANCE;
	}

	public List<Json> get() {
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
