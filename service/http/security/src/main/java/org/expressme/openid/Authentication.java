package org.expressme.openid;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication information returned from OpenID Provider.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 * @author Erwin Quinto (erwin.quinto@gmail.com)
 */
public class Authentication implements Serializable {

    private static final long serialVersionUID = -7031455449710566518L;

    private Map<String, String> data=new HashMap<String, String>();

    public String get(String fieldName){
    	return data.get(fieldName);
    }

    public void put(String fieldName,String fieldValue){
    	data.put(fieldName,fieldValue);
    }
    
    
    @Override
    public String toString() {
        return data.toString();
    }



	public Map<String, String> getData() {
		return data;
	}



	public void setData(Map<String, String> data) {
		this.data = data;
	}
}
