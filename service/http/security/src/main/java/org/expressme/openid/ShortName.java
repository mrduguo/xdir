package org.expressme.openid;

import java.util.HashMap;
import java.util.Map;

/**
 * Store short names which are mapping to providers' urls.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
public class ShortName {

    private Map<String, String> urlMap=new HashMap<String, String>();
    private Map<String, String> aliasMap=new HashMap<String, String>();
    
    String lookupUrlByName(String name) {
        return urlMap.get(name);
    }

    String lookupAliasByName(String name) {
        String alias = aliasMap.get(name);
        return alias==null ? Endpoint.DEFAULT_ALIAS : alias;
    }

	public Map<String, String> getUrlMap() {
		return urlMap;
	}

	public void setUrlMap(Map<String, String> urlMap) {
		this.urlMap = urlMap;
	}

	public Map<String, String> getAliasMap() {
		return aliasMap;
	}

	public void setAliasMap(Map<String, String> aliasMap) {
		this.aliasMap = aliasMap;
	}
}
