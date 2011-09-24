package org.duguo.xdir.http.json;

/**
 * serialize json object to a target object
 * 
 */
public interface JsonSerializer {
	
	/**
	 * convert the json object to another format
	 * 
	 * @param json
	 * @return
	 */
    public Object serialize(Json json);

}
