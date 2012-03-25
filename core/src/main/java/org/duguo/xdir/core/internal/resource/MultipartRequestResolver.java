package org.duguo.xdir.core.internal.resource;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

public interface MultipartRequestResolver {

	public HttpServletRequest resolveMultipartRequest(HttpServletRequest httpServletRequest) throws Exception;
	
	public File createTempFile() throws Exception;

}