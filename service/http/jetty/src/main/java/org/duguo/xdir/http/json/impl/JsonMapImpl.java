package org.duguo.xdir.http.json.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.duguo.xdir.http.json.JsonMap;
import org.duguo.xdir.http.json.Json;

public class JsonMapImpl extends AbstractJson implements JsonMap {
	
	private Map<String, Json> map=new LinkedHashMap<String, Json>();

	public JsonMapImpl(Object... values){
		if(values.length>1){
			for(int i=0;i<values.length/2;i++){
				add((String)values[i*2],values[i*2+1]);
			}
		}
	}

	public Json get(Object key) {
		Json value=map.get(key);
		if(value==null){
			value=JsonNull.instance();
		}
		return value;
	}
	
	public JsonMap add(String key, Object value) {
		Json json=null;
		if(value instanceof Json){
			json=(Json)value;
		}else{
			json=new JsonValueImpl(value);
		}
		
		if(map.containsKey(key)){
			Json existJson=map.get(key);
			JsonArrayImpl listJson=null;
			if(existJson instanceof JsonArrayImpl){
				listJson=(JsonArrayImpl) existJson;
			}else{
				listJson=new JsonArrayImpl(existJson);
				map.put(key,listJson.parent(this));
			}
			listJson.add(json.parent(listJson));
		}else{
			map.put(key, json.parent(this));
		}
		return this;
	}

	
	public JsonMap set(String key, Object value) {
		if(value==null){
			map.remove(key);
		}else if(value instanceof Json){
			Json jsonValue=(Json)value;
			if(jsonValue.isNull()){
				map.remove(key);
			}else{
				map.put(key, jsonValue.parent(this));
			}
		}else{
			map.put(key, new JsonValueImpl(value).parent(this));
		}
		return this;
	}
	public boolean isNull() {
		return false;
	}
	
	public boolean isMap() {
		return true;
	}

	public JsonMap map() {
		return this;
	}

	public Map<String, Json> get() {
		return map;
	}
}
