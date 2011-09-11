package org.duguo.xdir.spi.model;

public interface PathInfo
{


    public String[] getPaths();

    public String getPath( int index );

    public String getCurrentPath();

    public String getRemainPath();

    public String getFullPath();

    public void moveToNextPath();

    public int getCurrentIndex();

    public String getSubsetPaths( int startIndex,int endIndex, String prefix );

}
