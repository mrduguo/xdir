package org.duguo.xdir.core.internal.template.compressor;

import org.duguo.xdir.core.internal.template.Compressor;

public class HtmlCompressor implements Compressor
{
    private com.googlecode.htmlcompressor.compressor.HtmlCompressor compressor= new com.googlecode.htmlcompressor.compressor.HtmlCompressor();
    
    public String compress( String rawString ) throws Exception
    {
        return compressor.compress( rawString );
    }

    /**
     * @return
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#getYuiCssLineBreak()
     */
    public int getYuiCssLineBreak()
    {
        return compressor.getYuiCssLineBreak();
    }

    /**
     * @return
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#getYuiJsLineBreak()
     */
    public int getYuiJsLineBreak()
    {
        return compressor.getYuiJsLineBreak();
    }

    /**
     * @return
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#isCompressCss()
     */
    public boolean isCompressCss()
    {
        return compressor.isCompressCss();
    }

    /**
     * @return
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#isCompressJavaScript()
     */
    public boolean isCompressJavaScript()
    {
        return compressor.isCompressJavaScript();
    }

    /**
     * @return
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#isEnabled()
     */
    public boolean isEnabled()
    {
        return compressor.isEnabled();
    }

    /**
     * @return
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#isRemoveComments()
     */
    public boolean isRemoveComments()
    {
        return compressor.isRemoveComments();
    }

    /**
     * @return
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#isRemoveIntertagSpaces()
     */
    public boolean isRemoveIntertagSpaces()
    {
        return compressor.isRemoveIntertagSpaces();
    }

    /**
     * @return
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#isRemoveMultiSpaces()
     */
    public boolean isRemoveMultiSpaces()
    {
        return compressor.isRemoveMultiSpaces();
    }

    /**
     * @return
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#isRemoveQuotes()
     */
    public boolean isRemoveQuotes()
    {
        return compressor.isRemoveQuotes();
    }

    /**
     * @return
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#isYuiJsDisableOptimizations()
     */
    public boolean isYuiJsDisableOptimizations()
    {
        return compressor.isYuiJsDisableOptimizations();
    }

    /**
     * @return
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#isYuiJsNoMunge()
     */
    public boolean isYuiJsNoMunge()
    {
        return compressor.isYuiJsNoMunge();
    }

    /**
     * @return
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#isYuiJsPreserveAllSemiColons()
     */
    public boolean isYuiJsPreserveAllSemiColons()
    {
        return compressor.isYuiJsPreserveAllSemiColons();
    }

    /**
     * @param compressCss
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#setCompressCss(boolean)
     */
    public void setCompressCss( boolean compressCss )
    {
        compressor.setCompressCss( compressCss );
    }

    /**
     * @param compressJavaScript
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#setCompressJavaScript(boolean)
     */
    public void setCompressJavaScript( boolean compressJavaScript )
    {
        compressor.setCompressJavaScript( compressJavaScript );
    }

    /**
     * @param enabled
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#setEnabled(boolean)
     */
    public void setEnabled( boolean enabled )
    {
        compressor.setEnabled( enabled );
    }

    /**
     * @param removeComments
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#setRemoveComments(boolean)
     */
    public void setRemoveComments( boolean removeComments )
    {
        compressor.setRemoveComments( removeComments );
    }

    /**
     * @param removeIntertagSpaces
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#setRemoveIntertagSpaces(boolean)
     */
    public void setRemoveIntertagSpaces( boolean removeIntertagSpaces )
    {
        compressor.setRemoveIntertagSpaces( removeIntertagSpaces );
    }

    /**
     * @param removeMultiSpaces
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#setRemoveMultiSpaces(boolean)
     */
    public void setRemoveMultiSpaces( boolean removeMultiSpaces )
    {
        compressor.setRemoveMultiSpaces( removeMultiSpaces );
    }

    /**
     * @param removeQuotes
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#setRemoveQuotes(boolean)
     */
    public void setRemoveQuotes( boolean removeQuotes )
    {
        compressor.setRemoveQuotes( removeQuotes );
    }

    /**
     * @param yuiCssLineBreak
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#setYuiCssLineBreak(int)
     */
    public void setYuiCssLineBreak( int yuiCssLineBreak )
    {
        compressor.setYuiCssLineBreak( yuiCssLineBreak );
    }

    /**
     * @param yuiJsDisableOptimizations
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#setYuiJsDisableOptimizations(boolean)
     */
    public void setYuiJsDisableOptimizations( boolean yuiJsDisableOptimizations )
    {
        compressor.setYuiJsDisableOptimizations( yuiJsDisableOptimizations );
    }

    /**
     * @param yuiJsLineBreak
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#setYuiJsLineBreak(int)
     */
    public void setYuiJsLineBreak( int yuiJsLineBreak )
    {
        compressor.setYuiJsLineBreak( yuiJsLineBreak );
    }

    /**
     * @param yuiJsNoMunge
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#setYuiJsNoMunge(boolean)
     */
    public void setYuiJsNoMunge( boolean yuiJsNoMunge )
    {
        compressor.setYuiJsNoMunge( yuiJsNoMunge );
    }

    /**
     * @param yuiJsPreserveAllSemiColons
     * @see com.googlecode.htmlcompressor.compressor.HtmlCompressor#setYuiJsPreserveAllSemiColons(boolean)
     */
    public void setYuiJsPreserveAllSemiColons( boolean yuiJsPreserveAllSemiColons )
    {
        compressor.setYuiJsPreserveAllSemiColons( yuiJsPreserveAllSemiColons );
    }

}
