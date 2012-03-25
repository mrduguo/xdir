package org.duguo.xdir.http.client;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HttpClientUtil
{
	/**
	 * 
	 * @param httpUriRequest
	 * @param params name/value pair, or a map object which contain name/value pairs.
	 */
	@SuppressWarnings("unchecked")
	public static void addPostParameters(HttpPost httpUriRequest, Object... params) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();  
		if(params.length>1){
			for(int i=0;i<params.length/2;i++){
				nameValuePairs.add(new BasicNameValuePair((String)params[i*2], params[i*2+1].toString()));
			}
		}else if(params.length==1){
			Map<String,Object> paraMap=(Map<String,Object>)params[0];
			for(Map.Entry<String, Object> param:paraMap.entrySet()){
				nameValuePairs.add(new BasicNameValuePair(param.getKey(),param.getValue().toString()));
			}
		}
		try {

			UrlEncodedFormEntity httpEntity = new UrlEncodedFormEntity(nameValuePairs);
			httpUriRequest.setEntity(httpEntity);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}