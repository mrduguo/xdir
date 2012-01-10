package org.duguo.xdir.jcr.utils;

public class JcrPathUtils
{

    public static String normalizePathName(String rawName){
        String[] allparts=rawName.split(" ");
        StringBuffer newName=null;
        for(String str:allparts){
            if(newName==null){
                newName=new StringBuffer();
            }else{
                newName.append("_");
            }
            if(str.length()>0){
                // http://dev.day.com/docs/en/cq/current/javadoc/constant-values.html#com.day.cq.commons.jcr.JcrUtil.NON_VALID_CHARS
                str=str.replaceAll("[\\%\\*?\"\\[\\]|\n\t\r. \\\\]", "_");
                newName.append(str);
            }
        }
        return newName.toString();
    }


}
