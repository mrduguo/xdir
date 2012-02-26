package org.duguo.xdir.service.codec.yui;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import org.duguo.xdir.spi.service.DynamicService;
import org.duguo.xdir.spi.service.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlCompressCodec implements StringCodec,DynamicService
{
    private HtmlCompressor compressor;

    private static final Logger logger = LoggerFactory.getLogger(HtmlCompressCodec.class);

    public HtmlCompressCodec(){
        compressor= new HtmlCompressor();
        compressor.setCompressCss(isCompressCss());
        compressor.setCompressJavaScript(isCompressJs());
        compressor.setRemoveComments(isRemoveComments());
        compressor.setRemoveIntertagSpaces(isRemoveIntertagSpaces());
        compressor.setRemoveMultiSpaces(isRemoveMultiSpaces());
        compressor.setRemoveQuotes(isRemoveQuotes());
        compressor.setYuiCssLineBreak( JsMinifierCodec.getLineBreakPos() );
        compressor.setYuiJsDisableOptimizations( JsMinifierCodec.isDisableOptimizations() );
        compressor.setYuiJsNoMunge( !JsMinifierCodec.isMunge() );
        compressor.setYuiJsPreserveAllSemiColons(JsMinifierCodec.isPreserveSemiColons());
    }

    @Override
    public String apply(String input) throws Exception{
        if(logger.isTraceEnabled()) logger.trace("> apply {}",input);
        String result=compressor.compress( input );
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
        return System.getProperty("xdir.service.codec.html.service.name", "htmlCodec");
    }



    private boolean isCompressCss() {
        return Boolean.parseBoolean(System.getProperty("xdir.service.codec.html.compress.css", "true"));
    }
    private boolean isCompressJs() {
        return Boolean.parseBoolean(System.getProperty("xdir.service.codec.html.compress.js", "true"));
    }
    private boolean isRemoveComments() {
        return Boolean.parseBoolean(System.getProperty("xdir.service.codec.html.compress.remove.comments", "true"));
    }
    private boolean isRemoveIntertagSpaces() {
        return Boolean.parseBoolean(System.getProperty("xdir.service.codec.html.compress.remove.intertagspace", "true"));
    }
    private boolean isRemoveMultiSpaces() {
        return Boolean.parseBoolean(System.getProperty("xdir.service.codec.html.compress.remove.multispaces", "true"));
    }
    private boolean isRemoveQuotes() {
        return Boolean.parseBoolean(System.getProperty("xdir.service.codec.html.compress.remove.quotes", "false"));
    }


}
