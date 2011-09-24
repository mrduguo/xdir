package org.duguo.xdir.http.json.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.duguo.xdir.http.json.JsonValue;
import org.duguo.xdir.http.json.Json;

public class JsonValueImpl extends AbstractJson implements JsonValue{
	

	public static final String TIMESTAMP_STRING_1="yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	public static final String TIMESTAMP_STRING_2="yyyy-MM-dd'T'HH:mm:ssZ";
	public static final String TIMESTAMP_STRING_3="yyyy-MM-dd HH:mm:ss";
	public static final DateFormat TIMESTAMP_FORMAT_1 = new SimpleDateFormat(TIMESTAMP_STRING_1);
	public static final DateFormat TIMESTAMP_FORMAT_2 = new SimpleDateFormat(TIMESTAMP_STRING_2);
	public static final DateFormat TIMESTAMP_FORMAT_3 = new SimpleDateFormat(TIMESTAMP_STRING_3);
	
	public Object value;
	private Json parent;

	public JsonValueImpl(Object value){
		set(value);
	}
	
	public JsonValue set(Object value) {
		if(value instanceof JsonValue){
			this.value=((JsonValue)value).get();
		}else{
			this.value=value;
		}
		return this;
	}
	
	public boolean isValue(){
		return true;
	}
	public JsonValue value() {
		return this;
	}

	public Object get() {
		return value;
	}

	public Json get(Object key) {
		return JsonNull.instance();
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
	
	public boolean isNull() {
		return false;
	}
	
	public boolean isString(){
		return value instanceof String;
	}

	public String stringValue() {
		if(value ==null){
			return null;
		}else if(value instanceof String){
			return (String)value;
		}else{
			return String.valueOf(value);
		}
	}

	public boolean isBoolean() {
		if(!(value instanceof Boolean)){
			try{
				boolValue();
			}catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	public boolean boolValue() {
		if(!(value instanceof Boolean)){
			value=Boolean.parseBoolean(value.toString());
		}
		return (Boolean)value;
	}

	public boolean isInt() {
		if(!(value instanceof Integer)){
			try{
				intValue();
			}catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	public int intValue() {
		if(!(value instanceof Integer)){
			if(value instanceof Long){
				value=((Long)value).intValue();
			}else{
				value=Integer.parseInt(value.toString());
			}
		}
		return (Integer)value;
	}

	public boolean isLong() {
		if(!(value instanceof Long)){
			try{
				longValue();
			}catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	public long longValue() {
		if(!(value instanceof Long)){
			if(value instanceof Integer){
				value=((Integer)value).longValue();
			}else{
				value=Long.parseLong(value.toString());
			}
		}
		return (Long)value;
	}

	public boolean isDouble() {
		if(!(value instanceof Double)){
			try{
				doubleValue();
			}catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	public double doubleValue() {
		if(!(value instanceof Double)){
			if(value instanceof Integer){
				value=((Integer)value).doubleValue();
			}else if(value instanceof Long){
				value=((Long)value).doubleValue();
			}else{
				value=Double.parseDouble(value.toString());
			}
		}
		return (Double)value;
	}

	public boolean isDate() {
		if(!(value instanceof Date)){
			try{
				dateValue();
			}catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	public Date dateValue() {
		if(!(value instanceof Date)){
			if(isLong()){
				value=new Date((Long)value);
			}else{
				try{
					String dateString=(String)value;
					if(dateString.length()==TIMESTAMP_STRING_3.length()){
						value=TIMESTAMP_FORMAT_3.parse(dateString);
					}else if(dateString.length()==TIMESTAMP_STRING_2.length()){
						value=TIMESTAMP_FORMAT_2.parse(dateString);
					}else {
						value=TIMESTAMP_FORMAT_1.parse(dateString);					
					}
				}catch(ParseException ex){
					throw new RuntimeException("parse time ["+value+"] failed",ex);
				}
			}
		}
		return (Date)value;
	}
	
	public String toString(){
		return stringValue();
	}
}
