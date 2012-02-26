package org.duguo.xdir.core.internal.model;



import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.io.WriterOutputStream;
import org.duguo.xdir.spi.model.Model;
import org.duguo.xdir.spi.model.support.AbstractGetAndPut;
import org.duguo.xdir.core.internal.utils.RequestUtils;

public abstract class AbstractServletModel extends AbstractGetAndPut implements Model
{

    private PathInfoImpl pathInfo;
    private String format=FormatService.FORMAT_FOLDER;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private boolean advancedView;
    
    public ModelImpl getModel(){
        return (ModelImpl)this;
    }
    

    /*******************************************************
     * Modified setter
     *******************************************************/
    
    public boolean isRequestTrue(String paramName){
        return RequestUtils.isParamTrue( (ModelImpl)this, paramName );
    }

    public int getIntParameter(String paramName,int defaultValue){
        return RequestUtils.getIntParameter( (ModelImpl)this, paramName,defaultValue );
    }

    public String getStringParameter(String paramName){
        return RequestUtils.getStringParameter( (ModelImpl)this, paramName );
    }

    @SuppressWarnings("unchecked")
    public void mapPut(String mapName,String key,Object value ) throws Exception
    {
        Map map=(Map)get(mapName);
        if(map==null){
            map=new HashMap();
            put( mapName, map );
        }
        map.put( key, value);
    }

    public String readFileAsString(String fileName ) throws Exception
    {
        return FileUtils.readFileToString( new File(fileName) );
    }
    
    

    /*******************************************************
     * Setter and getters
     *******************************************************/
    
    public PathInfoImpl getPathInfo()
    {
        return pathInfo;
    }
    public void setPathInfo( PathInfoImpl pathInfo )
    {
        this.pathInfo = pathInfo;
    }
    public String getFormat()
    {
        return format;
    }
    public void setFormat( String format )
    {
        this.format = format;
    }
    public HttpServletRequest getRequest()
    {
        return request;
    }
    public void setRequest( HttpServletRequest request )
    {
        this.request = request;
    }
    public HttpServletResponse getResponse()
    {
        return response;
    }
    public OutputStream getResponseOutputStream() throws IOException
    {
        return new WriterOutputStream( getResponse().getWriter() );
    }
    public void setResponse( HttpServletResponse response )
    {
        this.response = response;
    }
    public boolean isAdvancedView() {
        return advancedView;
    }
    public void setAdvancedView(boolean advancedView) {
        this.advancedView = advancedView;
    }


}
