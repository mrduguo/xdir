package org.duguo.xdir.core.internal.cache;

import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

public abstract class AbstractCacheableResponse {

	private HttpServletResponse response;
	
	public AbstractCacheableResponse(HttpServletResponse response) {
		this.response=response;
	}
	
	public HttpServletResponse getResponse() {
		return response;
	}	
	

	// follwing are delegated methods
	
	public void addDateHeader(String arg0, long arg1) {
		throw new RuntimeException("Unimplemented method addDateHeader()");
	}
	public void addIntHeader(String arg0, int arg1) {
		throw new RuntimeException("Unimplemented method addIntHeader()");
	}
	public boolean containsHeader(String arg0) {
		return response.containsHeader(arg0);
	}
	public String encodeRedirectUrl(String arg0) {
		throw new RuntimeException("Deprecated method encodeRedirectUrl()");
	}
	public String encodeRedirectURL(String arg0) {
		return response.encodeRedirectURL(arg0);
	}
	public String encodeUrl(String arg0) {
		throw new RuntimeException("Deprecated method encodeUrl()");
	}
	public String encodeURL(String arg0) {
		return response.encodeURL(arg0);
	}
	public int getBufferSize() {
		return response.getBufferSize();
	}
	public String getCharacterEncoding() {
		return response.getCharacterEncoding();
	}
	public Locale getLocale() {
		return response.getLocale();
	}
	public boolean isCommitted() {
		return response.isCommitted();
	}
	public void reset() {
		response.reset();
	}
	public void resetBuffer() {
		response.resetBuffer();
	}
	public void setBufferSize(int arg0) {
		response.setBufferSize(arg0);
	}
	public void setCharacterEncoding(String arg0) {
		response.setCharacterEncoding(arg0);
	}
	public void setContentLength(int arg0) {
		response.setContentLength(arg0);
	}
	public void setDateHeader(String arg0, long arg1) {
		throw new RuntimeException("Unimplemented method setDateHeader()");
	}
	public void setIntHeader(String arg0, int arg1) {
		throw new RuntimeException("Unimplemented method setIntHeader()");
	}
	public void setLocale(Locale arg0) {
		throw new RuntimeException("Unimplemented method setLocale()");
	}
	public void setStatus(int arg0, String arg1) {
		throw new RuntimeException("Deprecated method setStatus(int arg0, String arg1)");
	}
}
