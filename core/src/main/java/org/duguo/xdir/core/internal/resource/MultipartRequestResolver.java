package org.duguo.xdir.core.internal.resource;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

public interface MultipartRequestResolver {

	public HttpServletRequest resolveMultipartRequest(HttpServletRequest httpServletRequest) throws Exception;
	
	public File createTempFile() throws Exception;

}