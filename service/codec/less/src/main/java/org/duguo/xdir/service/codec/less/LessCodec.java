package org.duguo.xdir.service.codec.less;

import com.asual.lesscss.LessEngine;
import org.duguo.xdir.spi.service.DynamicService;
import org.duguo.xdir.spi.service.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LessCodec implements StringCodec,DynamicService
{
    private static final Logger logger = LoggerFactory.getLogger(LessCodec.class);

    private LessEngine lessEngine = new LessEngine();

    @Override
    public String apply(String input) throws Exception{
        if(logger.isTraceEnabled()) logger.trace("> apply {}",input);
        String result = lessEngine.compile(input);
        if(logger.isTraceEnabled()) logger.trace("< apply {}",result);
        return result;
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
        return System.getProperty("xdir.service.codec.less.service.name","lessCodec");
    }
}
