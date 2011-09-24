package org.duguo.xdir.security.impl.url;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.spi.model.Model;
import org.duguo.xdir.security.api.url.SecureUrlManager;
import org.duguo.xdir.util.http.HttpUtil;

public class SecurityUrlManagerImpl implements SecureUrlManager
{
    
    private static final Logger logger = LoggerFactory.getLogger( SecurityUrlManagerImpl.class );
    
    private int httpPort=80;
    private int httpsPort=443;
    
    public boolean requireSecureUrl( Model model ) throws Exception{
        if(!hasSecurityUrl() || model.getRequest().getScheme().equals( HttpUtil.HTTPS )){
            return false;
        }else{
            String pageUrl=model.getPageUrl();
            if(pageUrl.charAt( 4 )!='s'){
                pageUrl=buildHttpsUrl( model );
            }
            model.getResponse().sendRedirect(pageUrl);
            return true;
        }
    }
    
    public boolean hasSecurityUrl(){
        return httpPort!=httpsPort;
    }
    
    public String buildHttpsUrl(Model model){
        String httpUrl=model.getPageUrl();
        String secureUrl=HttpUtil.HTTPS+httpUrl.substring( 4 );
        
        int splitIndex=secureUrl.indexOf( ":"+httpPort );
        if(splitIndex>0){
            if(httpsPort==443){
                secureUrl=secureUrl.substring( 0,splitIndex )+secureUrl.substring( splitIndex+String.valueOf( httpPort ).length()+1 );
            }else{
                secureUrl=secureUrl.substring( 0,splitIndex )+":"+httpsPort+secureUrl.substring( splitIndex+String.valueOf( httpPort ).length()+1 );
            }            
        }else if(httpsPort!=443){
            splitIndex=secureUrl.indexOf( "/",8 );
            secureUrl=secureUrl.substring( 0,splitIndex )+":"+httpsPort+secureUrl.substring( splitIndex+1 );
        }
        if(logger.isDebugEnabled())
            logger.debug("builded secure url [{}] from normal url [{}]",secureUrl,httpUrl);
        return secureUrl;
    }

    public int getHttpPort()
    {
        return httpPort;
    }

    public void setHttpPort( int httpPort )
    {
        this.httpPort = httpPort;
    }

    public int getHttpsPort()
    {
        return httpsPort;
    }

    public void setHttpsPort( int httpsPort )
    {
        this.httpsPort = httpsPort;
    }
}
