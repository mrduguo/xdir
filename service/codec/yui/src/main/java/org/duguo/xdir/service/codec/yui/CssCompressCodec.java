package org.duguo.xdir.service.codec.yui;

import org.duguo.xdir.spi.service.DynamicService;
import org.duguo.xdir.spi.service.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.io.StringWriter;

public class CssCompressCodec implements StringCodec,DynamicService
{
    private static final Logger logger = LoggerFactory.getLogger(CssCompressCodec.class);

    @Override
    public String apply(String input) throws Exception{
        if(logger.isTraceEnabled()) logger.trace("> apply {}",input);
        StringReader sr=new StringReader( input );
        com.yahoo.platform.yui.compressor.CssCompressor compressor= new com.yahoo.platform.yui.compressor.CssCompressor(sr);
        StringWriter result=new StringWriter();
        compressor.compress( result, getLineBreakPos());
        if(logger.isTraceEnabled()) logger.trace("< apply {}",result);
        return result.toString();
    }

    @Override
    public String apply(String input, Object model) throws Exception{
        return apply(input);
    }

    @Override
    public Object getServiceInstance() {
        return this;
    }

    @Override
    public String getServiceName() {
        return System.getProperty("xdir.service.codec.css.service.name", "cssCodec");
    }

    private int getLineBreakPos()
    {
        return Integer.parseInt(System.getProperty("xdir.service.codec.css.line.break.pos","10"));
    }
}
