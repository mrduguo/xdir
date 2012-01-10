package org.duguo.xdir.core.internal.app;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;


public class Tester
{

    /**
     * @param args
     */
    public static void main( String[] args )throws Exception
    {
        String path="http://localhost:8080/s/html_print.css";
        path=path.replaceAll(":", "/");
        File file=new File("/Users/gdu",path);
        if(!file.exists()){
        System.out.println("make:"+path);
        }
        System.out.println("make:"+path);
    }


}
