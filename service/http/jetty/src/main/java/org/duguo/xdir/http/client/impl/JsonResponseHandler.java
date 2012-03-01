package org.duguo.xdir.http.client.impl;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.http.json.JsonTextConverter;
import org.duguo.xdir.http.json.JsonUtil;
import org.duguo.xdir.spi.util.io.FileUtil;

public class JsonResponseHandler implements ResponseHandler<Json> {


	private JsonTextConverter jsonTextConverter;

    public Json handleResponse(final HttpResponse response)
            throws HttpResponseException, IOException {
    	Json result=null;
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() < 300) {
            HttpEntity entity = response.getEntity();
            if(entity != null){
            	result = handleEntity(response,entity);
            }
        }
        if(result==null){
        	result = handleFailureResponse(response,statusLine);
        }
        return result;
    }

	protected Json handleEntity(HttpResponse response,HttpEntity entity) throws IOException {
		Json result=jsonTextConverter.parse(entity.getContent());
		return result;
	}

	protected Json handleFailureResponse(HttpResponse response,StatusLine statusLine)throws IOException {
		Json result=JsonUtil.newMap(	"statusCode", statusLine.getStatusCode(),
								"statusReason", statusLine.getReasonPhrase()
							);
        HttpEntity entity = response.getEntity();
        if(entity != null){
        	String errorBody=FileUtil.readStreamAsString(entity.getContent());
        	result.map().set("errorBody", errorBody);
        }
		return result;
	}

	public JsonTextConverter getJsonTextConverter() {
		return jsonTextConverter;
	}

	public void setJsonTextConverter(JsonTextConverter jsonTextConverter) {
		this.jsonTextConverter = jsonTextConverter;
	}

}
