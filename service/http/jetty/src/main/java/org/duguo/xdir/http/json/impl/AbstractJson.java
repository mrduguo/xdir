package org.duguo.xdir.http.json.impl;

import java.util.Map;

import org.duguo.xdir.http.json.JsonArray;
import org.duguo.xdir.http.json.JsonMap;
import org.duguo.xdir.http.json.JsonValue;
import org.duguo.xdir.http.json.Json;

public abstract class AbstractJson implements Json{

	private Json parent;
	
	public JsonArray array() {
		return JsonArrayNull.instance();
	}
	public JsonMap map() {
		return JsonMapNull.instance();
	}
	public JsonValue value() {
		return JsonValueNull.instance();
	}
	
	public boolean isArray() {
		return false;
	}
	public boolean isMap() {
		return false;
	}
	public boolean isValue() {
		return false;
	}
	


	public Json parent() {
		if(parent==null){
			return JsonNull.instance();			
		}else{
			return parent;
		}
	}

	public Json parent(Json parent) {
		this.parent=parent;
		return this;
	}

	public String toString(){
		StringBuilder sb=new StringBuilder();
		toSimpleJson(this,sb);
		return sb.toString();
	}

	private void toSimpleJson(Json json,StringBuilder sb) {
		if(json.isValue()){
			if(json.isNull()){
				sb.append("null");
			}else{
				Object value=json.value().get();
				if(value instanceof Integer || value instanceof Long || value instanceof Double || value instanceof Boolean){
					sb.append(value);
				}else{
					sb.append("\"");
					sb.append(value);
					sb.append("\"");
				}
			}
		}else if(json.isMap()){
			sb.append("{");
			if(!json.isNull()){
				boolean isFirst=true;
				for(Map.Entry<String, Json> entry:json.map().get().entrySet()){
					if(isFirst){
						isFirst=false;
					}else{
						sb.append(", ");
					}
					sb.append("\"");
					sb.append(entry.getKey());
					sb.append("\":");
					toSimpleJson(entry.getValue(),sb);
				}
			}
			sb.append("}");
		}else if(json.isArray()){
			sb.append("[");
			if(!json.isNull()){
				boolean isFirst=true;
				for(Json entry:json.array().get()){
					if(isFirst){
						isFirst=false;
					}else{
						sb.append(", ");
					}
					toSimpleJson(entry,sb);
				}
			}
			sb.append("]");			
		}else{
			// json.isNull()
			sb.append("null");			
		}
	}

}
