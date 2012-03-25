package org.duguo.xdir.core.internal.cache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface CacheService<K, V>{
	void clearCache(String url);
    void initCache(String url);
    boolean isCacheExists(String url);
	HttpServletResponse handleRequest(HttpServletRequest request,HttpServletResponse response) throws IOException;
	void cacheResponse(HttpServletRequest request,CacheableResponse response) throws IOException;

}
