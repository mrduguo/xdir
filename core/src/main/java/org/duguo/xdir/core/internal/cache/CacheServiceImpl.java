package org.duguo.xdir.core.internal.cache;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;


public class CacheServiceImpl<K, V> extends AbstractCacheService implements CacheService<K, V>{
    private static final Logger LOG= LoggerFactory.getLogger(CacheServiceImpl.class);


	public void init() throws Exception {
		initDir();
	}

	public void clearCache(String url) {
        if(LOG.isTraceEnabled()) LOG.trace("> clearCache {}",url);
        url = resetToRootForChangeInJcrManager(url);
		File cacheFolder= getCacheFolderFromUrl(url);
        FileUtils.deleteQuietly(cacheFolder);
        if(LOG.isTraceEnabled()) LOG.trace("< clearCache {}");
	}

    private String resetToRootForChangeInJcrManager(String url) {
        if(url.endsWith("/admin/resources/jcr")){
            url="";
            if(LOG.isDebugEnabled()) LOG.debug("resetToRootForChangeInJcrManager {}",url);
        }
        return url;
    }

    public HttpServletResponse handleRequest(HttpServletRequest request,HttpServletResponse response) throws IOException {
        if(!request.getMethod().equals("GET") || request.getQueryString()!=null){
            return response;
        }
        File cacheFolder = buildCacheFolderFromRequest(request);
		File cacheInfo=new File(cacheFolder,AbstractCacheService.CACHE_INFO);
		if(cacheInfo.exists()){
			return verifyRoleAndWriteCache(request, response, cacheInfo);
		}else{
			return createCachableResponse(response, cacheFolder);
        }
	}
	
	public void cacheResponse(HttpServletRequest request,CacheableResponse response) throws IOException  {
        if(LOG.isTraceEnabled()) LOG.trace("> cacheResponse");
		WebPageCache webPageCache=response.getWebPageCache();
		setupHeaders(response, webPageCache);
		if(response.isCacheable()){
			storeAndWriteCache(request,response, webPageCache);
		}else{
			respondWithCache(request, response.getResponse(), webPageCache);
			webPageCache.getContent().delete();
		}
        if(LOG.isTraceEnabled()) LOG.trace("< cacheResponse");
	}

}
