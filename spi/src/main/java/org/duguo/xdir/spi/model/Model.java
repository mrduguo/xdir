package org.duguo.xdir.spi.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public interface Model extends GetAndPut{
    
    public static final String NULL_STRING="null";
    
    public PathInfo getPathInfo();
    public String getFormat();
    public HttpServletRequest getRequest();
    public HttpServletResponse getResponse();
    
    public String getPageUrl();
    public String getPagePath();
    public String getUserId();
    public void setUserId(String userId);
}
