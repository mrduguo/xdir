package org.duguo.xdir.http.json;

/*
JsonMapImpl.metaClass.getProperty = { String name ->
   delegate.get(name)
}
JsonArrayImpl.metaClass.getProperty = { String name ->
   delegate.get(name)
}


json=model.app.httpClient.httpGetXml("http://duguo.com/en/.atom");
System.out.println(JsonUtil.toText(json));
System.out.println(JsonUtil.toXml(json));
System.out.println("\n\n\n::"+json.feed.entry.get(2).title.text);

*/

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
