package org.duguo.xdir.osgi.bootstrap.conf;


import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

public abstract class AbstractProperties implements Map<Object, Object>
{
    @Resource(name="",description="")
    public static final String KEY_XDIR_DIR_HOME            = "xdir.dir.home";
    protected static final String KEY_XDIR_DIR_CONF            = "xdir.dir.conf";
    protected static final String KEY_XDIR_DIR_DATA            = "xdir.dir.data";
    protected static final String KEY_XDIR_DIR_VAR             = "xdir.dir.var";
    protected static final String KEY_XDIR_DIR_LOGS            = "xdir.dir.logs";
    protected static final String KEY_XDIR_DIR_BUNDLES         = "xdir.dir.bundles";
    
    protected static final String KEY_XDIR_OSGI_CMD_CONF       = "xdir.osgi.cmd.conf";
    protected static final String KEY_XDIR_OSGI_CMD_DEBUG      = "xdir.osgi.cmd.debug";
    protected static final String KEY_XDIR_OSGI_CMD_STOP       = "xdir.osgi.cmd.stop";
    protected static final String KEY_XDIR_OSGI_CMD_CONSOLE    = "xdir.osgi.cmd.console";
    protected static final String KEY_XDIR_OSGI_CMD_CLEAN      = "xdir.osgi.cmd.clean";
    public static final    String KEY_XDIR_OSGI_CMD_POLL       = "xdir.osgi.cmd.poll";
    public static final    String KEY_XDIR_OSGI_CMD_TIMEOUT    = "xdir.osgi.cmd.timeout";
    protected static final String KEY_XDIR_OSGI_CMD_IMPL       = "xdir.osgi.cmd.impl";
    protected static final String KEY_XDIR_OSGI_BUNDLES_CACHE  = "xdir.osgi.bundles.cache";
    protected static final String KEY_XDIR_OSGI_BUNDLES_SYSTEM = "xdir.osgi.bundles.system";
    protected static final String KEY_XDIR_OSGI_BUNDLES_USER   = "xdir.osgi.bundles.user";
    
    
    protected static final String VALUE_XDIR_OSGI_CMD_CONF         = "osgi.properties";
    protected static final String VALUE_XDIR_OSGI_BUNDLES_SYSTEM   = "system";
    protected static final String VALUE_XDIR_OSGI_BUNDLES_USER     = "user";
    protected static final String VALUE_XDIR_OSGI_BUNDLES_CORE     = "org.springframework.core,org.eclipse.gemini.blueprint.extender,xdir-core";

    
    private static final long serialVersionUID = 1L;
    
    private Map<Object, Object> systemProperties=System.getProperties();
    

    protected abstract String getXdirDirHome();
    
    
    public void putNewValueIfNotExist(String key,String value){
        if(!containsKey( key )){
            put( key, value );
        }
    }
    
    protected String retriveHomeRelatedDir(String key,String relativePath){
        String dir=getProperty(key);
        if(dir==null){
            dir=getXdirDirHome()+"/"+relativePath;
            putNewValueIfNotExist( key, dir );
        }
        return dir;
    }
    
    protected boolean isKeyValueTrue(String key, boolean defaultValue){
        String booleanValue=getProperty(key);
        if(booleanValue!=null){
            return "true".equals( booleanValue.toLowerCase() );
        }else{
            putNewValueIfNotExist( key, String.valueOf( defaultValue ) );
            return defaultValue;
        }
    }
        
	public String getProperty(String key) {
		return (String)get(key);
    }

    public boolean containsKey( Object key )
    {
        return systemProperties.containsKey( key );
    }
    public Object get( Object key )
    {
        return systemProperties.get( key );
    }
    public Set<Object> keySet()
    {
        return systemProperties.keySet();
    }

    public boolean containsValue( Object value )
    {
        return systemProperties.containsValue( value );
    }


    public Set<java.util.Map.Entry<Object, Object>> entrySet()
    {
        return systemProperties.entrySet();
    }


    public boolean isEmpty()
    {
        return false;
    }
    
    public Object put( Object key, Object value )
    {
        return systemProperties.put( key, value );
    }


    public void putAll( Map<? extends Object, ? extends Object> t )
    {
        systemProperties.putAll( t );
    }


    public Object remove( Object key )
    {
        return systemProperties.remove( key );
    }


    public int size()
    {
        return systemProperties.size();
    }


    public Collection<Object> values()
    {
        return systemProperties.values();
    }


    /*************************************************
     * Unsupported methods
     *************************************************/
    private void unsupported()
    {
        throw new UnsupportedOperationException();
    }

    public void clear()
    {
        unsupported();
    }



}