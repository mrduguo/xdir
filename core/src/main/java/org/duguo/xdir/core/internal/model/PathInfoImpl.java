package org.duguo.xdir.core.internal.model;

import org.duguo.xdir.spi.model.PathInfo;

public class PathInfoImpl implements PathInfo
{
    public String[] paths;
    public int processIndex;
    
    public PathInfoImpl(String[] paths) {
        this.paths = paths;
    }

    public void moveToNextPath() {
        processIndex++;
    }

    public void moveToPreviousPath()
    {
        processIndex--;
    }
    
    public String[] getPaths() {
        return paths;
    }
    
    public void setPaths(String[] paths) {
        this.paths=paths;
    }

    public String getPath(int index) {
        if (index < paths.length)
            return paths[index];
        else
            return null;
    }

    public int getCurrentIndex() {
        return processIndex;
    }

    public void setCurrentIndex(int index) {
        processIndex=index;
    }

    public String getCurrentPath() {
        return getPath(processIndex);
    }

    public String getCurrentAppPath() {
        return getSubsetPaths(0,processIndex,null);
    }

    public String getRemainPath() {
        return getSubsetPaths(processIndex,paths.length,null);
    }

    public String getFullPath() {
        return getSubsetPaths(0,paths.length,null);
    }

    public String getJcrPath(String jcrBase) {
        return getSubsetPaths(processIndex,paths.length,jcrBase);
    }
    
    public String toString(){
        return getFullPath();
    }

    public String getSubsetPaths(int startIndex,int endIndex,String prefix) {
        if (startIndex < paths.length){
            StringBuffer remainPaths=null;
            for(int i=startIndex;i<endIndex;i++){
                if(remainPaths==null){
                    remainPaths=new StringBuffer();
                    if(prefix!=null){
                        remainPaths.append( prefix );
                    }
                }
                remainPaths.append("/");
                remainPaths.append(paths[i]);
            }
            if(remainPaths!=null){
                return remainPaths.toString();
            }
        }
        return prefix;
    }
    
}
