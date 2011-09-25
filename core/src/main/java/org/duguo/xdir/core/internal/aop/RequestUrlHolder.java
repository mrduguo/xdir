package org.duguo.xdir.core.internal.aop;


public class RequestUrlHolder
{

    private static ThreadLocal<String> url=new ThreadLocal<String>();

    public static String get(){
        return url.get();
    }

    public static void set(String url){
        RequestUrlHolder.url.set(url);
    }
}
