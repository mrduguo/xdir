package org.duguo.xdir.spi.service;

import org.duguo.xdir.spi.model.Model;


/**
 * service provided to transform string to another format
 */
public interface StringCodec
{
    /**
     * transform the input string to another format
     *
     * @param input raw input string
     * @return  transformed text
     */
    public String apply(String input) throws Exception;

    /**
     * transform the input string to another format
     *
     *
     * @param input raw input string
     * @param model model object which can be used during the apply
     * @return  transformed text
     */
    public String apply(String input, Object model) throws Exception;
    
}
