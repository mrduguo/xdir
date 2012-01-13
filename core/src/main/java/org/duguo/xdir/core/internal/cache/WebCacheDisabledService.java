package org.duguo.xdir.core.internal.cache;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebCacheDisabledService implements CacheService<String, Object> {

    @Override
    public void clearCache(String url) {
    }

    @Override
    public void initCache(String url) {
    }

    @Override
    public boolean isCacheExists(String url) {
        return false;
    }

    @Override
    public HttpServletResponse handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		return response;
	}

    @Override
    public void cacheResponse(HttpServletRequest request, CacheableResponse response) throws IOException {
    }

}
