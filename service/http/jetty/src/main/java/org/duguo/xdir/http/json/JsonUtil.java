package org.duguo.xdir.http.json;

import org.duguo.xdir.http.json.impl.JsonArrayImpl;
import org.duguo.xdir.http.json.impl.JsonMapImpl;
import org.duguo.xdir.http.json.impl.JsonNull;
import org.duguo.xdir.http.json.impl.parser.JsonConverterUtil;
import org.duguo.xdir.http.json.impl.parser.XmlConverterUtil;

public class JsonUtil {
	
	public static JsonMap newMap(Object... values){
		return new JsonMapImpl(values);
	}
	
	public static JsonArray newArray(Object... values){
		return new JsonArrayImpl(values);
	}
	
	public static Json nullInstance(){
		return JsonNull.instance();
	}
	
	public static String toText(Json json){
		return JsonConverterUtil.toText(json);
	}
	
	public static String toXml(Json json){
		return XmlConverterUtil.toXml(json);
	}
	
	public static Json xmlToJson(String xml){
		return XmlConverterUtil.toJson(xml);
	}
	
	public static Json textToJson(String text){
		return JsonConverterUtil.toJson(text);
	}
	
	public static Json retrive(Json source,String... paths){
		if(paths.length==1){
			paths=paths[0].split("\\.");
		}
		for(String currentPath:paths){
			if(source.isArray() && !source.isNull()){
				try{
					int index=Integer.parseInt(currentPath);
					source=source.get(index);
				}catch(Exception ex){
					source=source.get(currentPath);
				}
			}else{
				source=source.get(currentPath);
			}
		}
		return source;
	}
}
