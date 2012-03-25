package org.duguo.xdir.osgi.bootstrap.osgiserver;


import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import java.util.*;


public class BundleUtils
{

    public static boolean isFragment( Bundle bundle )
    {
        Dictionary<String, String> headerMap = bundle.getHeaders();
        return headerMap.get( Constants.FRAGMENT_HOST ) != null;
    }

    public static String parseBundleStatus( int status )
    {
        switch ( status )
        {
            case Bundle.UNINSTALLED:
                return "UNINSTALLED";
            case Bundle.INSTALLED:
                return "INSTALLED";
            case Bundle.RESOLVED:
                return "RESOLVED";
            case Bundle.STARTING:
                return "STARTING";
            case Bundle.STOPPING:
                return "STOPPING";
            case Bundle.ACTIVE:
                return "ACTIVE";
            default:
                break;
        }
        return String.valueOf( status );
    }

    public static String bundleDisplayString( Bundle bundle,String msg )
    {
        StringBuilder sb=new StringBuilder();
        sb.append( "Bundle [");
        sb.append( bundle.getBundleId() );
        sb.append( ":");
        sb.append( bundle.getSymbolicName() );
        sb.append( ":");
        sb.append( bundle.getVersion().toString() );
        sb.append( "] ");
        sb.append( msg );
        return sb.toString();
    }

    public static Map<String, Map> parsePackage(String value)
    {
        Map<String, Map> packageHeader=new LinkedHashMap<String, Map>();
        String[] packagesDefination=parseDelimitedString(value,",");
        for(String singleItem:packagesDefination){
            Map<String, Object> packageInfo=new LinkedHashMap<String, Object>();
            String[] singlePackage=packagesDefination=parseDelimitedString(singleItem,";");
            if(singlePackage.length>1){
                for(int i=1;i<singlePackage.length;i++){
                    String[] attrs=packagesDefination=parseDelimitedString(singlePackage[i],"=");
                    if(attrs[1].indexOf( "," )>0){
                        packageInfo.put( attrs[0], parseDelimitedString(removeQuote(attrs[1]),",") );
                    }else{
                        packageInfo.put( attrs[0], removeQuote(attrs[1]) );
                    }
                }
            }
            packageHeader.put( singlePackage[0], packageInfo );
        }
        return packageHeader;
    }

    private static String removeQuote(String inputStr){
        if(inputStr.charAt( 0 )=='"'){
            return inputStr.substring( 1,inputStr.length()-1 );
        }else{
            return inputStr;
        }
    }

    /**
     * Copyed from felix ManifestParser
     * @seealso org.apache.felix.framework.util.manifestparser.ManifestParser.parseDelimitedString(String, delim)
     *
     * Parses delimited string and returns an array containing the tokens. This
     * parser obeys quotes, so the delimiter character will be ignored if it is
     * inside of a quote. This method assumes that the quote character is not
     * included in the set of delimiter characters.
     * @param value the delimited string to parse.
     * @param delim the characters delimiting the tokens.
     * @return an array of string tokens or null if there were no tokens.
     **/
    @SuppressWarnings("unchecked")
    private static String[] parseDelimitedString(String value, String delim)
    {
        if (value == null)
        {
            value = "";
        }

        List list = new ArrayList();

        int CHAR = 1;
        int DELIMITER = 2;
        int STARTQUOTE = 4;
        int ENDQUOTE = 8;

        StringBuffer sb = new StringBuffer();

        int expecting = (CHAR | DELIMITER | STARTQUOTE);

        for (int i = 0; i < value.length(); i++)
        {
            char c = value.charAt(i);

            boolean isDelimiter = (delim.indexOf(c) >= 0);
            boolean isQuote = (c == '"');

            if (isDelimiter && ((expecting & DELIMITER) > 0))
            {
                list.add(sb.toString().trim());
                sb.delete(0, sb.length());
                expecting = (CHAR | DELIMITER | STARTQUOTE);
            }
            else if (isQuote && ((expecting & STARTQUOTE) > 0))
            {
                sb.append(c);
                expecting = CHAR | ENDQUOTE;
            }
            else if (isQuote && ((expecting & ENDQUOTE) > 0))
            {
                sb.append(c);
                expecting = (CHAR | STARTQUOTE | DELIMITER);
            }
            else if ((expecting & CHAR) > 0)
            {
                sb.append(c);
            }
            else
            {
                throw new IllegalArgumentException("Invalid delimited string: " + value);
            }
        }

        if (sb.length() > 0)
        {
            list.add(sb.toString().trim());
        }

        return (String[]) list.toArray(new String[list.size()]);
    }

}
