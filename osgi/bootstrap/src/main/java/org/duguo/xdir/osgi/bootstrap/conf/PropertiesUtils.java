package org.duguo.xdir.osgi.bootstrap.conf;


import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.duguo.xdir.osgi.bootstrap.log.Logger;


public class PropertiesUtils
{
    private static final String DELIM_START = "${";
    private static final String DELIM_STOP = "}";


    public static String applyPlaceHoldersFromSystemProperties( String rawValue)
    {
    	String value = substVars( "", rawValue, System.getProperties(),System.getProperties(), null );
    	return value;
    }
    
    public static void replacePlaceHolders( Map<Object, Object> configuration,Properties newProperties )
    {
        for ( Enumeration e = newProperties.propertyNames(); e.hasMoreElements(); )
        {
            String key = ( String ) e.nextElement();
            if(!configuration.containsKey( key )){
                String value = substVars( key, newProperties.getProperty( key ), configuration,newProperties, null );
                if ( Logger.isDebugEnabled() )
                    Logger.debug( "Setup configuration for key [" + key + "] with value [" + value + "]" );
                configuration.put( key, value );
            }else{
                if(Logger.isDebugEnabled())
                    Logger.debug( "Cannot override system properties "+key+" from configuration file" );
            }
        }
    }


    @SuppressWarnings("unchecked")
    private static String substVars( String key, String val,Map<Object, Object> configuration, Properties configProps, Map cycleMap )
        throws IllegalArgumentException
    {
        // If there is currently no cycle map, then create
        // one for detecting cycles for this invocation.
        if ( cycleMap == null )
        {
            cycleMap = new HashMap();
        }

        // Put the current key in the cycle map.
        cycleMap.put( key, key );

        // Assume we have a value that is something like:
        // "leading ${foo.${bar}} middle ${baz} trailing"

        // Find the first ending '}' variable delimiter, which
        // will correspond to the first deepest nested variable
        // placeholder.
        int stopDelim = val.indexOf( DELIM_STOP );

        // Find the matching starting "${" variable delimiter
        // by looping until we find a start delimiter that is
        // greater than the stop delimiter we have found.
        int startDelim = val.indexOf( DELIM_START );
        while ( stopDelim >= 0 )
        {
            int idx = val.indexOf( DELIM_START, startDelim + DELIM_START.length() );
            if ( ( idx < 0 ) || ( idx > stopDelim ) )
            {
                break;
            }
            else if ( idx < stopDelim )
            {
                startDelim = idx;
            }
        }

        // If we do not have a start or stop delimiter, then just
        // return the existing value.
        if ( ( startDelim < 0 ) && ( stopDelim < 0 ) )
        {
            return val;
        }
        // At this point, we found a stop delimiter without a start,
        // so throw an exception.
        else if ( ( ( startDelim < 0 ) || ( startDelim > stopDelim ) ) && ( stopDelim >= 0 ) )
        {
            throw new IllegalArgumentException( "stop delimiter with no start delimiter: " + val );
        }

        // At this point, we have found a variable placeholder so
        // we must perform a variable substitution on it.
        // Using the start and stop delimiter indices, extract
        // the first, deepest nested variable placeholder.
        String variable = val.substring( startDelim + DELIM_START.length(), stopDelim );

        // Verify that this is not a recursive variable reference.
        if ( cycleMap.get( variable ) != null )
        {
            throw new IllegalArgumentException( "recursive variable reference: " + variable );
        }

        // Get the value of the deepest nested variable placeholder.
        // Try to configuration properties first.
        String substValue = ( configProps != null ) ? configProps.getProperty( variable, null ) : null;
        if ( substValue == null )
        {
            substValue = (String)configuration.get( variable);
            
            // Ignore unknown property values.
            if(substValue==null){
                if(Logger.isDebugEnabled())
                    Logger.debug( "Unknown property placeholder ["+variable+"] in ["+key+"], set to empty string" );
                substValue="";
            }
        }

        // Remove the found variable from the cycle map, since
        // it may appear more than once in the value and we don't
        // want such situations to appear as a recursive reference.
        cycleMap.remove( variable );

        // Append the leading characters, the substituted value of
        // the variable, and the trailing characters to get the new
        // value.
        val = val.substring( 0, startDelim ) + substValue
            + val.substring( stopDelim + DELIM_STOP.length(), val.length() );

        // Now perform substitution again, since there could still
        // be substitutions to make.
        val = substVars( key, val,configuration, configProps, cycleMap );

        // Return the value.
        return val;
    }
}