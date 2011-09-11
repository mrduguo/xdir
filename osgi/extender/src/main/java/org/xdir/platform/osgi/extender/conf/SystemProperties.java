package org.xdir.platform.osgi.extender.conf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class SystemProperties extends Properties
{
 
    private static final long serialVersionUID = 1L;

    public boolean contains( Object value )
    {
        return System.getProperties().contains( value );
    }

    public boolean containsKey( Object key )
    {
        return System.getProperties().containsKey( key );
    }

    public boolean containsValue( Object value )
    {
        return System.getProperties().containsValue( value );
    }

    public Enumeration<Object> elements()
    {
        return System.getProperties().elements();
    }

    public Set<java.util.Map.Entry<Object, Object>> entrySet()
    {
        return System.getProperties().entrySet();
    }

    public boolean equals( Object object )
    {
        return System.getProperties().equals( object );
    }

    public Object get( Object key )
    {
        return System.getProperties().get( key );
    }

    public String getProperty( String key, String defaultValue )
    {
        return System.getProperties().getProperty( key, defaultValue );
    }

    public String getProperty( String key )
    {
        return System.getProperties().getProperty( key );
    }

    public boolean isEmpty()
    {
        return System.getProperties().isEmpty();
    }

    public Enumeration<Object> keys()
    {
        return System.getProperties().keys();
    }

    public Set<Object> keySet()
    {
        return System.getProperties().keySet();
    }

    public void list( PrintStream out )
    {
        System.getProperties().list( out );
    }

    public void list( PrintWriter out )
    {
        System.getProperties().list( out );
    }

    public void load( InputStream inStream ) throws IOException
    {
        System.getProperties().load( inStream );
    }

    public void load( Reader arg0 ) throws IOException
    {
        System.getProperties().load( arg0 );
    }

    public void loadFromXML( InputStream in ) throws IOException, InvalidPropertiesFormatException
    {
        System.getProperties().loadFromXML( in );
    }

    public Enumeration<?> propertyNames()
    {
        return System.getProperties().propertyNames();
    }

    public Object put( Object key, Object value )
    {
        return System.getProperties().put( key, value );
    }

    public void putAll( Map<? extends Object, ? extends Object> map )
    {
        System.getProperties().putAll( map );
    }

    public Object remove( Object key )
    {
        return System.getProperties().remove( key );
    }

    @SuppressWarnings("deprecation")
    public void save( OutputStream out, String comments )
    {
        System.getProperties().save( out, comments );
    }

    public Object setProperty( String key, String value )
    {
        return System.getProperties().setProperty( key, value );
    }

    public int size()
    {
        return System.getProperties().size();
    }

    public void store( OutputStream out, String comments ) throws IOException
    {
        System.getProperties().store( out, comments );
    }

    public void store( Writer arg0, String arg1 ) throws IOException
    {
        System.getProperties().store( arg0, arg1 );
    }

    public void storeToXML( OutputStream os, String comment, String encoding ) throws IOException
    {
        System.getProperties().storeToXML( os, comment, encoding );
    }

    public void storeToXML( OutputStream os, String comment ) throws IOException
    {
        System.getProperties().storeToXML( os, comment );
    }

    public Set<String> stringPropertyNames()
    {
        return System.getProperties().stringPropertyNames();
    }

    public Collection<Object> values()
    {
        return System.getProperties().values();
    }

    /*********************************************
     * Unsupported methods
     *********************************************/
    public void clear()
    {
        unsupported("clear");
    }

    private void unsupported(String method)
    {
        throw new UnsupportedOperationException(method);
    }

}
