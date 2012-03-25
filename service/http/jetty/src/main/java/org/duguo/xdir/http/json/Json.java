package org.duguo.xdir.http.json;


public interface Json {
	
	public Object get();
	public Json get(Object key);
	public boolean isNull();
	
	public Json parent();
	public Json parent(Json parent);
	
	public boolean isMap();
	public JsonMap map();
	
	public boolean isArray();
	public JsonArray array();
	
	public boolean isValue();
	public JsonValue value();
	
}
