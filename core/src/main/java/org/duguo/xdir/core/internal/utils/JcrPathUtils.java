package org.duguo.xdir.core.internal.utils;

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
                str=str.replaceAll("[^-\\.:A-Za-z0-9/]", "_");
                newName.append(str);
            }
        }
        return newName.toString();
    }


}
