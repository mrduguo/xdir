package org.duguo.xdir.http.client.impl;

import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.duguo.xdir.spi.service.DynamicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.http.client.HttpClientService;
import org.duguo.xdir.http.client.HttpClientUtil;
import org.duguo.xdir.http.json.Json;

public class DefaultHttpClientService implements HttpClientService,DynamicService{
	
	public static final Logger logger = LoggerFactory.getLogger( DefaultHttpClientService.class );
    
	
	public Map<String,ResponseHandler<Object>> responseHandlers;

	
	public HttpClient createClient(){
		return new DefaultHttpClient();
	}

	public ResponseHandler<Object> retriveResponseHandler(String format) {
		return responseHandlers.get(format);
	}

	public Json httpGet(String url,String format){
		return httpRequestGet(url,format);
	}

	public Json httpGetJson(String url){
		return httpRequestGet(url,"json");
	}

	public Json httpGetXml(String url){
		return httpRequestGet(url,"xml");
	}

	public Json httpGetText(String url){
		return httpRequestGet(url,"text");
	}

	public Json httpPost(String url,String format,Object... params){
		return httpRequestPost(url,format,params);
	}

	public Object httpRequest(HttpUriRequest httpUriRequest,ResponseHandler<Object> responseHandler,HttpClient httpClient){
		if(responseHandler==null){
			responseHandler=retriveResponseHandler("text");
		}
		if(httpClient==null){
			httpClient=createClient();
		}
        try{
	        Object response = httpClient.execute(httpUriRequest, responseHandler);
	        httpClient.getConnectionManager().shutdown();
	        if(logger.isDebugEnabled())
	        	logger.debug("http "+httpUriRequest.getMethod()+" [{}] response:\n{}",httpUriRequest.getURI().toString(),response);
	        return response;
        }catch(Exception ex){
        	throw new RuntimeException("failed to send http "+httpUriRequest.getMethod()+" request to url ["+httpUriRequest.getURI().toString()+"]",ex);
        }
	}
	
	public Json httpRequestGet(String url,String format){
		return (Json)httpRequest( new HttpGet(url),responseHandlers.get(format),createClient());
	}
	
	public Json httpRequestPost(String url,String format,Object... params){
		HttpPost httpUriRequest = new HttpPost(url);
		
		if(params.length>0){
			HttpClientUtil.addPostParameters(httpUriRequest, params);
		}
		return (Json)httpRequest( httpUriRequest,responseHandlers.get(format),createClient());
	}


	public Map<String, ResponseHandler<Object>> getResponseHandlers() {
		return responseHandlers;
	}

	public void setResponseHandlers(
			Map<String, ResponseHandler<Object>> responseHandlers) {
		this.responseHandlers = responseHandlers;
	}

    @Override
    public Object getServiceInstance() {
        return this;
    }

    @Override
    public String getServiceName() {
        return System.getProperty("xdir.service.httpclent.service.name","httpClient");
    }
}
