package org.duguo.xdir.core.internal.resource;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;

public class FileUploadHttpServletRequest implements HttpServletRequest{
	private HttpServletRequest httpServletRequest;
	private Hashtable<String, Object> params;
	
	public FileUploadHttpServletRequest(HttpServletRequest httpServletRequest,Hashtable<String, Object> params){
		this.httpServletRequest=httpServletRequest;
		this.params=params;
	}

	public HttpServletRequest getRequest() {
		return httpServletRequest;
	}

	public File getFile(String name) {
		return (File)params.get(name);
	}


	public String getParameter(String name) {
		return (String)params.get(name);
	}

	public Hashtable<String, Object> getParameterMap() {
		return params;
	}

	public Enumeration<String> getParameterNames() {
		return params.keys();
	}

	public String[] getParameterValues(String name) {
		return new String[]{(String)params.get(name)};
	}
	

	public Object getAttribute(String name) {
		return httpServletRequest.getAttribute(name);
	}

	@SuppressWarnings("unchecked")
	public Enumeration getAttributeNames() {
		return httpServletRequest.getAttributeNames();
	}

	public String getAuthType() {
		return httpServletRequest.getAuthType();
	}

	public String getCharacterEncoding() {
		return httpServletRequest.getCharacterEncoding();
	}

	public int getContentLength() {
		return httpServletRequest.getContentLength();
	}

	public String getContentType() {
		return httpServletRequest.getContentType();
	}

	public String getContextPath() {
		return httpServletRequest.getContextPath();
	}

	public Cookie[] getCookies() {
		return httpServletRequest.getCookies();
	}

	public long getDateHeader(String name) {
		return httpServletRequest.getDateHeader(name);
	}

	public String getHeader(String name) {
		return httpServletRequest.getHeader(name);
	}

	@SuppressWarnings("unchecked")
	public Enumeration getHeaderNames() {
		return httpServletRequest.getHeaderNames();
	}

	@SuppressWarnings("unchecked")
	public Enumeration getHeaders(String name) {
		return httpServletRequest.getHeaders(name);
	}

	public ServletInputStream getInputStream() throws IOException {
		return httpServletRequest.getInputStream();
	}

	public int getIntHeader(String name) {
		return httpServletRequest.getIntHeader(name);
	}

	public String getLocalAddr() {
		return httpServletRequest.getLocalAddr();
	}

	public Locale getLocale() {
		return httpServletRequest.getLocale();
	}

	@SuppressWarnings("unchecked")
	public Enumeration getLocales() {
		return httpServletRequest.getLocales();
	}

	public String getLocalName() {
		return httpServletRequest.getLocalName();
	}

	public int getLocalPort() {
		return httpServletRequest.getLocalPort();
	}

	public String getMethod() {
		return httpServletRequest.getMethod();
	}

	public String getPathInfo() {
		return httpServletRequest.getPathInfo();
	}

	public String getPathTranslated() {
		return httpServletRequest.getPathTranslated();
	}

	public String getProtocol() {
		return httpServletRequest.getProtocol();
	}

	public String getQueryString() {
		return httpServletRequest.getQueryString();
	}

	public BufferedReader getReader() throws IOException {
		return httpServletRequest.getReader();
	}

	@SuppressWarnings("deprecation")
	public String getRealPath(String path) {
		return httpServletRequest.getRealPath(path);
	}

	public String getRemoteAddr() {
		return httpServletRequest.getRemoteAddr();
	}

	public String getRemoteHost() {
		return httpServletRequest.getRemoteHost();
	}

	public int getRemotePort() {
		return httpServletRequest.getRemotePort();
	}

	public String getRemoteUser() {
		return httpServletRequest.getRemoteUser();
	}

	public RequestDispatcher getRequestDispatcher(String path) {
		return httpServletRequest.getRequestDispatcher(path);
	}

	public String getRequestedSessionId() {
		return httpServletRequest.getRequestedSessionId();
	}

	public String getRequestURI() {
		return httpServletRequest.getRequestURI();
	}

	public StringBuffer getRequestURL() {
		return httpServletRequest.getRequestURL();
	}

	public String getScheme() {
		return httpServletRequest.getScheme();
	}

	public String getServerName() {
		return httpServletRequest.getServerName();
	}

	public int getServerPort() {
		return httpServletRequest.getServerPort();
	}

	public String getServletPath() {
		return httpServletRequest.getServletPath();
	}

	public HttpSession getSession() {
		return httpServletRequest.getSession();
	}

	public HttpSession getSession(boolean create) {
		return httpServletRequest.getSession(create);
	}

	public Principal getUserPrincipal() {
		return httpServletRequest.getUserPrincipal();
	}

	public boolean isRequestedSessionIdFromCookie() {
		return httpServletRequest.isRequestedSessionIdFromCookie();
	}

	@SuppressWarnings("deprecation")
	public boolean isRequestedSessionIdFromUrl() {
		return httpServletRequest.isRequestedSessionIdFromUrl();
	}

	public boolean isRequestedSessionIdFromURL() {
		return httpServletRequest.isRequestedSessionIdFromURL();
	}

	public boolean isRequestedSessionIdValid() {
		return httpServletRequest.isRequestedSessionIdValid();
	}

	public boolean isSecure() {
		return httpServletRequest.isSecure();
	}

	public boolean isUserInRole(String role) {
		return httpServletRequest.isUserInRole(role);
	}

	public void removeAttribute(String name) {
		httpServletRequest.removeAttribute(name);
	}

	public void setAttribute(String name, Object o) {
		httpServletRequest.setAttribute(name, o);
	}

	public void setCharacterEncoding(String env)
			throws UnsupportedEncodingException {
		httpServletRequest.setCharacterEncoding(env);
	}
	
	
	
}
