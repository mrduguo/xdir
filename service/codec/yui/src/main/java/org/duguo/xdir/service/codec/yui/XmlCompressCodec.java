package org.duguo.xdir.service.codec.yui;

import com.googlecode.htmlcompressor.compressor.XmlCompressor;
import org.duguo.xdir.spi.service.DynamicService;
import org.duguo.xdir.spi.service.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlCompressCodec implements StringCodec,DynamicService
{
    private XmlCompressor compressor;

    private static final Logger logger = LoggerFactory.getLogger(XmlCompressCodec.class);

    public XmlCompressCodec(){
        compressor= new XmlCompressor();
        compressor.setRemoveComments(isRemoveComments());
        compressor.setRemoveIntertagSpaces(isRemoveIntertagSpaces());
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
        return System.getProperty( "xdir.service.codec.xml.service.name", "xmlCodec");
    }


    private boolean isRemoveComments() {
        return Boolean.parseBoolean(System.getProperty("xdir.service.codec.html.compress.remove.comments", "true"));
    }
    private boolean isRemoveIntertagSpaces() {
        return Boolean.parseBoolean(System.getProperty("xdir.service.codec.html.compress.remove.intertagspace", "true"));
    }

}
