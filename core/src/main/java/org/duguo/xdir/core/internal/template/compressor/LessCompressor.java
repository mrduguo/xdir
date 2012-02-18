package org.duguo.xdir.core.internal.template.compressor;

import com.asual.lesscss.LessEngine;
import com.asual.lesscss.LessOptions;
import org.duguo.xdir.core.internal.template.Compressor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class LessCompressor implements Compressor
{
    private static final Logger logger = LoggerFactory.getLogger(LessCompressor.class);

    private LessEngine lessEngine = new LessEngine();

    public String compress( String rawString ) throws Exception
    {
        if(logger.isTraceEnabled()) logger.trace("> compress {}",rawString);
        String result = lessEngine.compile(rawString);
        if(logger.isTraceEnabled()) logger.trace("< compress {}",result);
        return result;
    }

    public LessEngine getLessEngine() {
        return lessEngine;
    }

    public void setLessEngine(LessEngine lessEngine) {
        this.lessEngine = lessEngine;
    }
}
