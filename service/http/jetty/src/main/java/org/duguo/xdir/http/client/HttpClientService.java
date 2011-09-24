package org.duguo.xdir.http.client;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.duguo.xdir.http.json.Json;


public interface HttpClientService
{

	public Json httpGetJson(String url);

	public Json httpGetXml(String url);

	public Json httpGetText(String url);
	
	public Json httpGet(String url,String format);
	
	public Json httpPost(String url,String format,Object... params);
	
	public Object httpRequest(HttpUriRequest httpUriRequest, ResponseHandler<Object> responseHandler, HttpClient httpClient);

	public HttpClient createClient();
	
	public ResponseHandler<Object> retriveResponseHandler(String format);
}