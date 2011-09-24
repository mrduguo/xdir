package org.duguo.xdir.http.service;


import javax.servlet.Servlet;


public interface ServletService
{

    public void register( String alias, Servlet servlet ) throws Exception;


    public void unregister( String alias ) throws Exception;

}