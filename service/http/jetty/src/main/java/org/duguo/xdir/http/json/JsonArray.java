package org.duguo.xdir.http.json;


import java.util.List;

import org.duguo.xdir.http.json.Json;

public interface JsonArray extends Json{
	

	public JsonArray array();
	
	public List<Json> get();
	
	public Json select(String paths,String value);
	
	public JsonArray add(Object value);
	
}
