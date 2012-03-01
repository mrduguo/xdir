package org.duguo.xdir.spi.util.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapUtil
{
    @SuppressWarnings("unchecked")
    public static Map sort(Map sourceMap){
        if(sourceMap==null || sourceMap.size()==0){
            return sourceMap;
        }
        
        List  keys=new ArrayList();
        for(Object key:sourceMap.keySet()){
            keys.add( key );
        }
        Collections.sort( keys );
        
        Map targetMap=new LinkedHashMap();
        for(Object key:keys){
            targetMap.put( key, sourceMap.get( key ) );
        }
        return targetMap;
    }
}
