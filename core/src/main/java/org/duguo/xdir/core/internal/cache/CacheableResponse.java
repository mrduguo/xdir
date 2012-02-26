package org.duguo.xdir.core.internal.cache;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class CacheableResponse extends AbstractCacheableResponse implements HttpServletResponse {

    public static final int CACEHE_ENABLED=1;
    public static final int CACEHE_DISABLED=-1;
	public static final int REQUEST_PROCESSED=-2;
	
	private int cacheable=CACEHE_ENABLED;
	private WebPageCache webPageCache;
	private PrintWriter printWriter;
	private OutputStream outputStream;
	private Map<String,String> headers=new HashMap<String,String>();
	
	public CacheableResponse(HttpServletResponse response,WebPageCache webPageCache) {
		super(response);
		this.webPageCache = webPageCache;
	}
	public WebPageCache getWebPageCache() {
		return webPageCache;
	}
	public boolean isCacheable() {
		return cacheable == CACEHE_ENABLED;
	}
	public boolean isRequestProcessed() {
		return cacheable == REQUEST_PROCESSED;
	}
	public void setRequestProcessed() {
		cacheable = REQUEST_PROCESSED;
	}
	public void disableCache() {
        this.cacheable = CACEHE_DISABLED;
	}
	public ServletOutputStream getOutputStream() throws IOException {
		checkWriterOrOutputStream();
		outputStream=new FileOutputStream(webPageCache.getContent());
		return new CacheOutputStream(outputStream);
	}
	public PrintWriter getWriter() throws IOException {
		checkWriterOrOutputStream();
		printWriter= new PrintWriter(getOutputStream());
		return printWriter;
	}
	public String getContentType() {
		return webPageCache.getContentType();
	}
	public void setContentType(String contentType) {
		webPageCache.setContentType(contentType);
	}

	public void flushBuffer() throws IOException {
		if(printWriter!=null){
			printWriter.flush();
			printWriter.close();
		}else if(outputStream!=null){
			outputStream.flush();
			outputStream.close();
		}
	}

	public void addHeader(String key, String value){
		headers.put(key,value);
	}	
	public void setHeader(String key, String value) {
		headers.put(key,value);
	}

	public void sendError(int arg0, String arg1) throws IOException {
		cacheable=REQUEST_PROCESSED;
		getResponse().sendError(arg0, arg1);
	}
	public void sendError(int arg0) throws IOException {
		cacheable=REQUEST_PROCESSED;
		getResponse().sendError(arg0);
	}
	public void sendRedirect(String arg0) throws IOException {
		cacheable=REQUEST_PROCESSED;
		getResponse().sendRedirect(arg0);
	}
	public void setStatus(int status) {
		cacheable=REQUEST_PROCESSED;
		if(headers.size()>0){
			for(Map.Entry<String, String> entry:headers.entrySet()){
				getResponse().addHeader(entry.getKey(), entry.getValue());
			}
		}
		getResponse().setStatus(status);
	}

	public void addCookie(Cookie arg0) {
		if(this.cacheable==CACEHE_ENABLED){
			cacheable=CACEHE_DISABLED;
		}
		getResponse().addCookie(arg0);
	}

	private void checkWriterOrOutputStream()throws IOException{
		if(webPageCache.getContent().length()>0){
			throw new RuntimeException("the response already opened"+webPageCache.getContent().length());
		}
	}
	public Map<String,String> getHeaders() {
		return headers;
	}
}
