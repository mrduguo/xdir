package org.duguo.xdir.http.json.impl;

import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.http.json.JsonValue;

public class JsonValueNull extends JsonValueImpl{
	
	private static final JsonValueNull NULL_INSTANCE=new JsonValueNull();

	// singleton only
	private JsonValueNull(){
		super(null);
	}
	
	public static JsonValue instance(){
		return NULL_INSTANCE;
	}

	public Json get(Object key) {
		return JsonNull.instance();
	}

	public boolean isNull() {
		return true;
	}

}
