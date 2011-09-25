package org.duguo.xdir.core.internal.template.compressor;

import org.duguo.xdir.core.internal.template.Compressor;

public class XmlCompressor implements Compressor
{
    private com.googlecode.htmlcompressor.compressor.XmlCompressor compressor= new com.googlecode.htmlcompressor.compressor.XmlCompressor();
    
    public String compress( String rawString ) throws Exception
    {
        return compressor.compress( rawString );
    }

    /**
     * @return
     * @see com.googlecode.htmlcompressor.compressor.XmlCompressor#isEnabled()
     */
    public boolean isEnabled()
    {
        return compressor.isEnabled();
    }

    /**
     * @return
     * @see com.googlecode.htmlcompressor.compressor.XmlCompressor#isRemoveComments()
     */
    public boolean isRemoveComments()
    {
        return compressor.isRemoveComments();
    }

    /**
     * @return
     * @see com.googlecode.htmlcompressor.compressor.XmlCompressor#isRemoveIntertagSpaces()
     */
    public boolean isRemoveIntertagSpaces()
    {
        return compressor.isRemoveIntertagSpaces();
    }

    /**
     * @param enabled
     * @see com.googlecode.htmlcompressor.compressor.XmlCompressor#setEnabled(boolean)
     */
    public void setEnabled( boolean enabled )
    {
        compressor.setEnabled( enabled );
    }

    /**
     * @param removeComments
     * @see com.googlecode.htmlcompressor.compressor.XmlCompressor#setRemoveComments(boolean)
     */
    public void setRemoveComments( boolean removeComments )
    {
        compressor.setRemoveComments( removeComments );
    }

    /**
     * @param removeIntertagSpaces
     * @see com.googlecode.htmlcompressor.compressor.XmlCompressor#setRemoveIntertagSpaces(boolean)
     */
    public void setRemoveIntertagSpaces( boolean removeIntertagSpaces )
    {
        compressor.setRemoveIntertagSpaces( removeIntertagSpaces );
    }


}
