package org.duguo.xdir.http.json.impl.parser;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.http.json.JsonMap;
import org.duguo.xdir.http.json.JsonParser;
import org.duguo.xdir.http.json.impl.JsonMapImpl;


/**
 * Help class to convert http request parameters into json object.
 * 
 */
public class RequestParametersParser implements JsonParser{

    private static final Logger logger = LoggerFactory.getLogger( RequestParametersParser.class );
    
    /**
     * convert http request params to json object. ignore multiple values.
     *
     * @param source  HttpServletRequest instance
     */
	public Json parse(Object source){
		HttpServletRequest request=(HttpServletRequest)source;
    	JsonMap rootJson=parseRequest(request);
        return rootJson;
	}
    
    @SuppressWarnings("unchecked")
	public static JsonMap parseRequest(HttpServletRequest request){
    	JsonMap rootJson=new JsonMapImpl();
    	Enumeration<String> paramsNames = request.getParameterNames();
    	while(paramsNames.hasMoreElements()){
    		String key=paramsNames.nextElement();
    		rootJson.set(key, request.getParameter(key));
    	}
    	if(logger.isDebugEnabled())
    		logger.debug("parsed json object:\n{}",rootJson);
        return rootJson;
    	
    }

}
