package org.duguo.xdir.spi.security;

import org.duguo.xdir.spi.model.GetAndPut;
import org.duguo.xdir.spi.model.Model;

public interface SecurityService extends GetAndPut
{
    boolean requireSecureUrl(Model model) throws Exception;
    void accessDenied(Model model,String loginUrl);
    
    /**
     * verify user are logged in already with valid session
     * @param model
     * @return
     */
    boolean isAuthenticated(Model model);

    void logout(Model model);
    User login(Model model);
    
    String getUserId(Model model);
    User getUser(Model model);
    
    boolean hasRole(Model model,int role);
    boolean inGroup(Model model,String groupPath, Object... aclInfo);
    boolean hasGroupRole(Model model,String groupPath,int role, Object... aclInfo);
    int groupRole(Model model,String groupPath, Object... aclInfo);
    
    String radomString(int length);
    String radomNumber(int length);
    
    String encode(String provider,String secret);
    String digest(String secret);
    String encrypt(String secret);
    String decrypt(String secret);

}
