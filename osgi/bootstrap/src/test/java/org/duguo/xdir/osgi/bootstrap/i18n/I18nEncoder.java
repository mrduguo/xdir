package org.duguo.xdir.osgi.bootstrap.i18n;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class I18nEncoder
{
    public static void main(String...strings )throws Exception{
        File inputFile=new File( "/Users/gdu/projects/github/mrduguo/xdir/osgi/bootstrap/src/main/resources/org/duguo/xdir/osgi/bootstrap/i18n/Messages_zh_CN.txt" );

        BufferedReader input = new BufferedReader( new FileReader( inputFile ) );
        try
        {
            String line=null;
            while((line=input.readLine())!=null){
                System.out.println(escape(line));
            }
        }
        finally
        {
            input.close();
        }
    }
    
    private static String charToHex(char c) {
        StringBuffer buffer = new StringBuffer();
        if (c <= 0x7E) {
            buffer.append(c);
        } else {
            buffer.append("\\u");
            String hex = Integer.toHexString(c);
            for (int j = hex.length(); j < 4; j++ ) {
                buffer.append('0');
            }
            buffer.append(hex);
        }
        return buffer.toString();
    }

    public static String escape(String s){
        if(s==null)
            return null;
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<s.length();i++){
            char ch=s.charAt(i);
            switch(ch){
            case '\n':
                sb.append("\\n");
                break;
            case '\r':
                sb.append("\\r");
                break;
            case '\t':
                sb.append("\\t");
                break;
            default:
                sb.append(charToHex(ch));
            }
        }//for
        return sb.toString();
    }
    
}
