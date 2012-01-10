package org.duguo.xdir.core.internal.utils;



import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.duguo.xdir.jcr.utils.JcrPathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.core.internal.model.ModelImpl;

public class RequestUtils
{
    private static final Logger logger = LoggerFactory.getLogger( RequestUtils.class );

    
    public static final String PARAM_PATH="_path";
    public static final String PARAM_TITLE="_title";
    
    public static String getPath(ModelImpl model){
        return getStringParameter( model, PARAM_PATH );
    }
    public static String getTitle( ModelImpl model )
    {
        String value=model.getRequest().getParameter(PARAM_TITLE);
        if(value!=null){
            value=value.trim();
            return value;
        }
        return null;
    }
    
    public static String retriveStringParameter(ModelImpl model,String param,String defaultValue){
        String value=model.getRequest().getParameter( param );
        return value==null?defaultValue:value;
    }
        
    public static String getStringParameter(ModelImpl model,String paramName) {
        return model.getRequest().getParameter(paramName);
    }

    public static boolean isParamTrue( ModelImpl model, String paramName )
    {
        return "true".equals( getStringParameter( model, paramName ) );
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, String> getProperties( ModelImpl model )
    {
        Map<String, String> properties = new LinkedHashMap<String, String>();
        List<String> parameterNames = new LinkedList<String>();
        Enumeration<String> namesEnumeration = model.getRequest().getParameterNames();
        while(namesEnumeration.hasMoreElements()){
            String paramName=namesEnumeration.nextElement();
            if(paramName.startsWith("properties")){
                parameterNames.add(paramName);
            }
        }
        
        if(parameterNames.size()>0){
            Collections.sort(parameterNames);
            for(int i=0;i<parameterNames.size()/2;i++){
                String key=model.getRequest().getParameter(parameterNames.get(i*2)).trim();
                String value=model.getRequest().getParameter(parameterNames.get(i*2+1)).trim();
                if(key.startsWith("/")){
                    key=key.substring(1);
                }
                if(key.endsWith("/")){
                    key=key.substring(key.length()-1);
                }
                if(key.length()>0){
                    properties.put(JcrPathUtils.normalizePathName(key), value);
                }
            }
        }
        return properties;
    }
    
    public static int getIntParameter( ModelImpl model, String paramName,int defaultValue )
    {
        String value=getStringParameter( model, paramName );
        if(value!=null){
            try{
                return Integer.parseInt( value );
            }catch(Exception ex){
                if(logger.isDebugEnabled())
                    logger.debug( "failed to parse int value [{}]",value );
            }
        }
        return defaultValue;
    }
}
