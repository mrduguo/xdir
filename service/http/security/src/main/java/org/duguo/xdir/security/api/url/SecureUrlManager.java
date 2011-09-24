package org.duguo.xdir.security.api.url;


import org.duguo.xdir.spi.model.Model;


public interface SecureUrlManager
{

    public boolean requireSecureUrl( Model model ) throws Exception;
    
    public boolean hasSecurityUrl();
    
    public String buildHttpsUrl( Model model );

}