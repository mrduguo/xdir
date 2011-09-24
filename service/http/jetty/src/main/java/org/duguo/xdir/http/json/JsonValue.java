package org.duguo.xdir.http.json;

import java.util.Date;

import org.duguo.xdir.http.json.Json;

public interface JsonValue extends Json{

	public Object get();
	public JsonValue set(Object value);
	
	public JsonValue value();

	public boolean isString();
	public String stringValue();

	public boolean isBoolean();
	public boolean boolValue();

	public boolean isInt();
	public int intValue() ;

	public boolean isLong();
	public long longValue();

	public boolean isDouble();
	public double doubleValue() ;

	public boolean isDate();
	public Date dateValue() ;
	
}
