package org.duguo.xdir.spi.service;

public interface TextCompressService
{
    /**
     * 
     * @param format supported format e.g. css, js, html, xml. But will up to the service implementation.
     * @param rawString
     * @return
     */
    public String compress(String format, String rawString);
    
}
