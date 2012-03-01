package org.duguo.xdir.spi.util.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtil
{
    private static final Logger logger = LoggerFactory.getLogger( HttpUtil.class );

    public static final String METHOD_GET="GET";
    public static final String HTTP="http";
    public static final String HTTPS="https";
    private static final String PREFIX_HTTP=HTTP+"://";
    private static final String PREFIX_HTTPS=HTTPS+"://";
    private static final String PORT_HTTP=":80/";
    private static final String PORT_HTTPS=":443/";
        
    public static String normalizeUrl(String rawUrl){
        if(!rawUrl.endsWith( "/" )){
            rawUrl=rawUrl+"/";
        }
        if(rawUrl.startsWith( PREFIX_HTTP ) && rawUrl.indexOf( PORT_HTTP )>0){
            rawUrl=rawUrl.substring( 0, rawUrl.indexOf( PORT_HTTP ))+rawUrl.substring( rawUrl.indexOf( PORT_HTTP )+PORT_HTTP.length());
        }
        if(rawUrl.startsWith( PREFIX_HTTPS ) && rawUrl.indexOf( PORT_HTTPS )>0){
            rawUrl=rawUrl.substring( 0, rawUrl.indexOf( PORT_HTTPS ))+rawUrl.substring( rawUrl.indexOf( PORT_HTTPS )+PORT_HTTPS.length());
        }
        if(rawUrl.endsWith( "/" )){
            rawUrl=rawUrl.substring( 0, rawUrl.length()-1);
        }
        return rawUrl;
    }
    
    public static String retriveUriFromUrl(String url){
        int splitterIndex=url.indexOf( "/", 8 );
        String uri=null;
        if(splitterIndex<0){
            uri="";
        }else{
            uri=url.substring( splitterIndex );
        }
        
        if(logger.isDebugEnabled())
            logger.debug( "retrived uri [{}] url [{}]",uri,url );
        return uri;
    }
    
    public static String retriveContextPathFromServletAlias(String servletAlias){
        String servletContext=servletAlias;
        int splitterIndex=servletContext.lastIndexOf( "/" );
        if(splitterIndex>0){
            // sample: /admin/*
            servletContext=servletContext.substring( 0,splitterIndex);
        }else{
            // sample: /*
            servletContext="";
        }
        splitterIndex=servletContext.lastIndexOf( ":" );
        if(splitterIndex>0){
            // sample: /admin:/admin/*
            servletContext=servletContext.substring(splitterIndex+1);
        }else{
            // sample: /admin           
        }
        if(logger.isDebugEnabled())
            logger.debug( "retrived context path [{}] from alias [{}]",servletContext,servletAlias );
        return servletContext;
    }
    
    public static String normalizeServletAlias(String servletAlias){
        String normalizedAlias=servletAlias;
        int splitterIndex=normalizedAlias.indexOf( "/" );
        if(splitterIndex==0){
            // sample: /admin
            // alias is started with slash
        }else{
            // sample: admin
            normalizedAlias="/"+normalizedAlias;
        }
        
        splitterIndex=normalizedAlias.indexOf( "*" );
        if(splitterIndex<0){
            if(normalizedAlias.length()==1){
                // sample: /
                normalizedAlias="/*";
            }else{
                // sample: /admin
                normalizedAlias=normalizedAlias+":"+normalizedAlias+"/*";                
            }
        }else{
            // sample: /*
            // the alias already normalized
        }
        if(logger.isDebugEnabled())
            logger.debug( "normalized servlet alias [{}] from alias [{}]",normalizedAlias,servletAlias );
        return normalizedAlias;
    }


    /**
     * parentUrl must be end with / if it link to a folder
     * 
     * @param parentUrl
     * @param name
     * @return
     */
    public static String autoDetectRelativeUrl( String parentUrl, String name )
    {
        String relativeUrl=null;
        int lastIndexOfSlash=parentUrl.lastIndexOf("/");
        if(lastIndexOfSlash==parentUrl.length()-1){
            // sample http://localhost/
            relativeUrl=parentUrl+name+"/";
        }else{
            String parentFile=parentUrl.substring( lastIndexOfSlash+1);
            int formatSpliter=parentFile.lastIndexOf( "$" );
            // sample http://localhost/about${model.format}
            if(formatSpliter<=0){
                // sample http://localhost/about.html
                formatSpliter=parentFile.lastIndexOf( "." );
                if(formatSpliter<=0){
                    throw new RuntimeException("cannot detect relative url based on parent url ["+parentUrl+"] with name ["+name+"]");
                }
            }
            String format=parentFile.substring(formatSpliter);
            parentFile=parentFile.substring(0,formatSpliter);
            if(parentFile.equals( "index" )){
                // sample http://localhost/index.html
                relativeUrl=parentUrl.substring( 0,lastIndexOfSlash+1)+name+format;
            }else{
                // sample http://localhost/blog.html
                relativeUrl=parentUrl.substring( 0,lastIndexOfSlash+1)+parentFile+"/"+name+format;
            }
        }
        if(logger.isDebugEnabled())
            logger.debug( "auto detect relative url [{}] from [{}]",relativeUrl, parentUrl);
        return relativeUrl;
    }


    public static String resolveBaseUrl( String linkUrl )
    {
        String baseUrl=null;
        int lastIndexOfSlash=linkUrl.lastIndexOf( "/" );
        if(lastIndexOfSlash==linkUrl.length()-1){
            // sample http://localhost/
            baseUrl=linkUrl;
        }else{
            String parentFile=linkUrl.substring( lastIndexOfSlash+1);
            int formatSpliter=parentFile.lastIndexOf( "$" );
            // sample http://localhost/about${model.format}
            if(formatSpliter<=0){
                // sample http://localhost/about.html
                formatSpliter=parentFile.lastIndexOf( "." );
                if(formatSpliter<=0){
                    throw new RuntimeException("cannot detect base url based ["+linkUrl+"]");
                }
            }
            String format=parentFile.substring(formatSpliter);
            parentFile=parentFile.substring(0,formatSpliter);
            if(parentFile.equals( "index" )){
                // sample http://localhost/index.html
                baseUrl=linkUrl.substring( 0,lastIndexOfSlash+1);
            }else{
                // sample http://localhost/blog.html
                baseUrl=linkUrl.substring( 0,lastIndexOfSlash+1)+parentFile+"/";
            }
        }
        if(logger.isDebugEnabled())
            logger.debug( "detect base url [{}] from [{}]",baseUrl, linkUrl);
        return baseUrl;
    }
    

    public static String getClientIp(HttpServletRequest request){
        String clientIp=request.getHeader("X-Forwarded-For");
        if(clientIp==null){
            clientIp=request.getRemoteAddr();
        }
        return clientIp;
    }
    

    public static String encodeParam(String param){
    	try {
			return URLEncoder.encode(param, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("never happens",e);
		}
    }
    

    public static String decodeParam(String param){
    	try {
			return URLDecoder.decode(param, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("never happens",e);
		}
    }
}
