package org.duguo.xdir.osgi.bootstrap.i18n;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class MessagesGenerator
{
    public static void main(String... args) throws Exception{
        args=new String[]{
            "/data/projects/xdirplatform/source/platform/osgi/xdir-osgi-bootstrap/src/test/resources",
            "/data/projects/xdirplatform/source/platform/osgi/xdir-osgi-bootstrap/target/classes",
            Messages.class.getName()};
        
        for(int i=2;i<args.length;i++){
            processSingleClass(args[0],args[1], args[i] );
        }
    }

    @SuppressWarnings("unchecked")
    private static void processSingleClass(String inputFolder,String outputFolder, String sourceClassName ) throws ClassNotFoundException,
        IllegalAccessException
    {
        File targetFile=null;
        Class sourceClass=Messages.class.getClassLoader().loadClass( sourceClassName );
        Map<String,String> messages=new Hashtable<String,String>();
        for(Field field:sourceClass.getFields()){
            if(field.getType()==String.class && Modifier.isStatic( field.getModifiers() ) &&  Modifier.isPublic( field.getModifiers() )){
                String fieldName=field.getName().replaceAll( "_",".").toLowerCase();
                String fieldValue=(String) field.get( null );
                messages.put( fieldName, fieldValue );
            }
        }
        messages=sortByKey( messages );
        int keySpace=0;
        for(String key:messages.keySet()){
            if(key.length()>keySpace){
                keySpace=key.length();
            }
        }
        Map<String,String> codeCache=new Hashtable<String,String>();
        writeKeysWithPrefix( targetFile,messages, keySpace,codeCache, "ERROR MESSAGES", "error" );
        appendToFile(targetFile,"");
        writeKeysWithPrefix( targetFile,messages, keySpace,codeCache, "WARNING MESSAGES", "warn" );
        appendToFile(targetFile,"");
        writeKeysWithPrefix( targetFile,messages, keySpace,codeCache, "INFO MESSAGES", "info" );
        appendToFile(targetFile,"");
        writeKeysWithPrefix( targetFile,messages, keySpace,codeCache, "MESSAGE STRINGS", "string" );
    }
    
    private static void writeKeysWithPrefix(File targetFile,Map<String,String> messages,int keySpace,Map<String,String> codeCache,String title,String prefix){
        appendTitle( targetFile,keySpace,title );
        char msgType=prefix.substring( 0,1 ).toUpperCase().charAt( 0 );
        for(String key:messages.keySet()){
            if(key.startsWith( prefix )){
                StringBuilder line=new StringBuilder();
                line.append( key );
                for(int i=0;i<keySpace-key.length();i++){
                    line.append( " " );
                }
                line.append( " =" );
                line.append( messages.get( key ).replaceAll( "\n", "\\\\n" ));
                appendToFile(targetFile,line.toString() );

                if(msgType!='S'){
                    String code=messages.get( key );
                    if(code.substring( 0,1 ).equals( "<" ) && code.length()>13 && code.substring( 11,13 ).equals( "> " )){
                        code=code.substring( 1,11 );
                        if(msgType!=code.charAt( 9 )){
                            System.out.println("WARN invalid code type extension for code:\n"+key+"="+code);
                        }
                    }else{
                        System.out.println("WARN message without code:\n"+key+"="+code);
                    }
                    if(codeCache.containsKey(  code )){
                        System.out.println("WARN duplicated key found:\n"+key+"="+messages.get( key )+"\n"+codeCache.get( code )+"=<"+code+"> ...");
                    }else{
                        codeCache.put( code,key);
                    }
                }
            }
        }
    }

    private static void appendTitle(File targetFile, int keySpace,String title )
    {
        StringBuilder line=new StringBuilder();
        for(int i=0;i<keySpace+14;i++){
            line.append( "#" );
        }
        line.append( "\n########  " );
        line.append( title);
        for(int i=0;i<keySpace-title.length()-6;i++){
            line.append( " " );
        }
        line.append( "  ########\n" );
        for(int i=0;i<keySpace+14;i++){
            line.append( "#" );
        }
        appendToFile(targetFile,line.toString() );
    }

    private static void appendToFile(File targetFile, String line )
    {
        System.out.println(line);
    }

    @SuppressWarnings("unchecked")
    private static Map<String,String> sortByKey(Map<String,String> map) {
        List<Map.Entry<String,String>> list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                  return ((Comparable) ((Map.Entry) (o1)).getKey())
                 .compareTo(((Map.Entry) (o2)).getKey());
             }
        });
        Map<String,String> result = new LinkedHashMap<String,String>();
        for (Iterator<Map.Entry<String,String>> it = list.iterator(); it.hasNext();) {
             Map.Entry<String,String> entry = (Map.Entry<String,String>)it.next();
             result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

}
