package org.duguo.xdir.http.json.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.duguo.xdir.http.json.JsonArray;
import org.duguo.xdir.http.json.Json;

public class JsonArrayImpl extends AbstractJson implements JsonArray{
	
	private List<Json> array=new ArrayList<Json>();


	public JsonArrayImpl(Object... values){
		if(values.length>0){
			for(Object value:values){
				add(value);
			}
		}
	}
	
	public boolean isArray() {
		return true;
	}

	public Json get(Object key) {
		if(key instanceof String){
			if(array.size()>0){
				return array.get(0).get(key);
			}			
		}else{
			int index;
			if(key instanceof Integer){
				index=(Integer)key;
			}else if(key instanceof Long){
				index=((Long)key).intValue();
			}else if(key instanceof BigDecimal){
				index=((BigDecimal)key).intValue();
			}else{
				return array.get(0).get(key.toString());
			}
			if(array.size()>index){
				return array.get(index);
			}
		}
		return JsonNull.instance();
	}

	public Json select(String paths,String value) {
		String[] pathsArray=paths.split(",");
		for(Json json:array){
			Json testObject=json;
			for(String path:pathsArray){
				testObject=testObject.get(path);
				if(testObject.isNull()){
					break;
				}
			}
			if(!testObject.isNull()){
				if(value==null || (testObject.isValue() && testObject.value().equals(value))){
					return json;
				}
			}
		}
		return JsonNull.instance();
	}

	public JsonArray add(Object value) {
		if(value instanceof Json){
			array.add(((Json)value).parent(this));
		}else{
			array.add(new JsonValueImpl(value).parent(this));
		}
		return this;
	}
	public boolean isNull() {
		return false;
	}

	public JsonArray array() {
		return this;
	}

	public List<Json> get() {
		return array;
	}
}
