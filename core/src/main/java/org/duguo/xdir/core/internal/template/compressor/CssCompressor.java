package org.duguo.xdir.core.internal.template.compressor;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.duguo.xdir.core.internal.template.Compressor;

public class CssCompressor implements Compressor
{
    
    private int linebreakpos=10;
    
    public String compress( String rawString ) throws Exception
    {
        StringReader sr=new StringReader( rawString );
        com.yahoo.platform.yui.compressor.CssCompressor compressor= new com.yahoo.platform.yui.compressor.CssCompressor(sr);
        StringWriter sw=new StringWriter();
        compressor.compress( sw,linebreakpos);
        return sw.toString();
    }

    /**
     * @return the linebreakpos
     * @see com.yahoo.platform.yui.compressor.CssCompressor#compress(Writer, int)
     */
    public int getLinebreakpos()
    {
        return linebreakpos;
    }

    /**
     * @param linebreakpos the linebreakpos to set
     * @see com.yahoo.platform.yui.compressor.CssCompressor#compress(Writer, int)
     */
    public void setLinebreakpos( int linebreakpos )
    {
        this.linebreakpos = linebreakpos;
    }



}
