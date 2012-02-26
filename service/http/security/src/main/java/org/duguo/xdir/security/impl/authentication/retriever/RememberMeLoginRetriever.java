package org.duguo.xdir.security.impl.authentication.retriever;


import javax.servlet.http.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.spi.security.LoginEvent;
import org.duguo.xdir.security.api.authentication.LoginRetriever;
import org.duguo.xdir.security.api.codec.StringEncoder;

public class RememberMeLoginRetriever implements LoginRetriever {

    private static final Logger logger = LoggerFactory.getLogger(RememberMeLoginRetriever.class);

    private String rememberMeCookieKey = "rm";
    private StringEncoder rememberMeDecoder;

    public void retrieve(LoginEvent loginEvent) {
        Cookie[] cookies = loginEvent.getRequest().getCookies();
        String userId = null;
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(rememberMeCookieKey)) {
                    String cookieValue = rememberMeDecoder.encode(cookie.getValue());
                    if (cookieValue != null) {
                        String[] cookieInfo = cookieValue.split(":");
                        if (cookieInfo.length == 2 && Long.parseLong(cookieInfo[1]) > System.currentTimeMillis()) {
                            userId = cookieInfo[0];
                            loginEvent.setRememberMe(true);
                            if (logger.isDebugEnabled())
                                logger.debug("retrived remember me cookie [{}]", userId);
                        }
                    }
                    break;
                }
            }
        }
        loginEvent.setUserName(userId);
    }

    public StringEncoder getRememberMeDecoder() {
        return rememberMeDecoder;
    }

    public void setRememberMeDecoder(StringEncoder rememberMeDecoder) {
        this.rememberMeDecoder = rememberMeDecoder;
    }

    public String getRememberMeCookieKey() {
        return rememberMeCookieKey;
    }

    public void setRememberMeCookieKey(String rememberMeCookieKey) {
        this.rememberMeCookieKey = rememberMeCookieKey;
    }
}
