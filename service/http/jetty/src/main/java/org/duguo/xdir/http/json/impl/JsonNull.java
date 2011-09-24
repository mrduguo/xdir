package org.duguo.xdir.http.json.impl;

import org.duguo.xdir.http.json.Json;


public class JsonNull extends AbstractJson{
	
	private static final JsonNull NULL_INSTANCE=new JsonNull();

	// singleton only
	private JsonNull(){
	}
	
	public static Json instance(){
		return NULL_INSTANCE;
	}

	public Json get(Object key) {
		return NULL_INSTANCE;
	}
	
	public Object get() {
		return null;
	}
	
	public boolean isNull() {
		return true;
	}
	
	public String toString(){
		return null;
	}

    protected final Object clone() {
        return this;
    }
    
    public boolean equals(Object object) {
        return object == null || object == this;
    }
    
}
