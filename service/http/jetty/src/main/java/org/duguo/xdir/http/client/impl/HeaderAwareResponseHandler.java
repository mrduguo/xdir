package org.duguo.xdir.http.client.impl;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.http.json.JsonMap;
import org.duguo.xdir.http.json.JsonUtil;

import java.io.IOException;

public class HeaderAwareResponseHandler extends JsonResponseHandler {
	
	protected Json handleEntity(HttpResponse response,HttpEntity entity) throws IOException {
		Json result=super.handleEntity(response, entity);
		JsonMap headers=JsonUtil.newMap();
		for(Header header:response.getAllHeaders()){
			headers.set(header.getName(), header.getValue());
		}
		result.map().add("headers", headers);
		return result;
	}

}
