package org.duguo.xdir.core.internal.app;

import java.util.Map;
import java.util.TreeMap;


public class Tester
{

    /**
     * @param args
     */
    public static void main( String[] args )throws Exception
    {
        System.out.println("a.jar".matches( ".*jar" ));
        Map<String, String> map=new TreeMap<String, String>();
        map.put("a", "1");
        map.put("c", "3");
        map.put("b", "2");
        System.out.println(map);
        for(Map.Entry<String, String> en:map.entrySet()){
        	System.out.println(en.getKey());
        }

    }


}
