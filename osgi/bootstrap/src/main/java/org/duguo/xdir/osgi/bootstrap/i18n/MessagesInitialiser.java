package org.duguo.xdir.osgi.bootstrap.i18n;


import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;


public class MessagesInitialiser
{

    private static final Map<String, String> messages = new HashMap<String, String>();

    /**
     * 
     * @param msgs message pattern and arguments: msgs[0] is the pattern, rest will be passed as formatter arguments
     * @return
     */
    public static String format( String... msgs )
    {
        if ( msgs.length == 1 )
        {
            return msgs[0];
        }
        else
        {
            MessageFormat formatter = new MessageFormat( msgs[0] );
            Object[] params = new Object[msgs.length - 1];
            for ( int i = 1; i < msgs.length; i++ )
            {
                params[i - 1] = msgs[i];
            }
            return formatter.format( params );
        }
    }


    public static String parseCombinedMessage( Class<?> messagesClass, String msg )
    {
        String[] msgs = msg.split( "," );
        String text = null;
        try
        {
            Field field = messagesClass.getField( msgs[0] );
            if ( field != null )
            {
                text = ( String ) field.get( null );
            }
            else
            {
                text = msgs[0].toLowerCase().replaceAll( "_", "." );
                text = messages.get( text );
            }
        }
        catch ( Throwable ex )
        {
            throw new RuntimeException( ex );
        }
        if ( text != null )
        {
            msgs[0] = text;
            return format( msgs );
        }
        else
        {
            return null;
        }
    }


    /**
     * 
     * @param messagesClass The class contain messages
     */
    public static void init( Class<?> messagesClass, String messagesFile )
    {
        try
        {
            MessagesLoader.load( messages,messagesFile );
            for ( Field field : messagesClass.getFields() )
            {
                String key = field.getName().toLowerCase().replaceAll( "_", "." );
                if ( messages.containsKey( key ) )
                {
                    String localeValue = messages.get( key );
                    field.set( messagesClass, localeValue );
                    messages.remove( key );
                }
            }
        }
        catch ( MissingResourceException ignore )
        {
        }
        catch ( Throwable ex )
        {
            throw new RuntimeException( ex );
        }
    }



}
