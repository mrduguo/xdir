package org.duguo.xdir.osgi.bootstrap.log;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.duguo.xdir.osgi.bootstrap.conf.OsgiProperties;
import org.duguo.xdir.osgi.bootstrap.i18n.Messages;


/**
 * Logger debug flag initialised in OsgiPropertiesFactory.
 * Logger log file initialised after command parsed inside CommandFactory
 * 
 * 
 * @author Guo Du
 */
public abstract class Logger
{
    private static boolean debugEnabled = false;
    private static SimpleDateFormat logDateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss,SSS" );
    private static List<String> cachedMessage = new ArrayList<String>();

    private static ConsoleDataOutput consoleDataOutput;

    public static void setDebugEnabled( boolean debugEnabled )
    {
        Logger.debugEnabled = debugEnabled;
    }

    public static void init(OsgiProperties configuration)
    {
        File logFile = new File( configuration.retriveXdirDirLogs(), "system.log" );
        boolean consoleEnabled=configuration.isConsoleEnabled();
        verifyLogsFolder( logFile );
        consoleDataOutput = new ConsoleDataOutput( logFile, ( debugEnabled || consoleEnabled ) );
        flushCachedMessage();
        if ( Logger.isDebugEnabled() )
            Logger.debug( "Logger inited with debug ["+(Logger.isDebugEnabled()?"on":"off")+"] and log file [" + logFile.getPath() +"]");
    }


    public static boolean isDebugEnabled()
    {
        return debugEnabled;
    }


    public static boolean hasMessageCode( String msg )
    {
        return msg!=null && msg.charAt( 0 ) == '<';
    }


    public static ConsoleDataOutput getConsoleDataOutput()
    {
        return consoleDataOutput;
    }


    public static void showLogFileLocation()
    {
        log( Messages.INFO_XDIR_CMD_LOG_LOCATION, consoleDataOutput.consoleLogFile.getPath());
    }

    

    public static void log( String... msgs )
    {
        switch ( msgs[0].charAt( 10 ) )
        {
            case 'E':
                writeMeg( Messages.STRING_LOGGER_LEVEL_ERROR, msgs );       
                break;
            case 'W':
                writeMeg( Messages.STRING_LOGGER_LEVEL_WARN, msgs );       
                break;
            case 'I':
                writeMeg( Messages.STRING_LOGGER_LEVEL_INFO, msgs );       
                break;
            default:
                writeMeg( Messages.STRING_LOGGER_LEVEL_UNKNOWN, msgs );
                break;
        }
    }



    public static void log( Throwable ex, String... msgs )
    {
        log(msgs );
        if ( ex != null )
        {
            ex.printStackTrace();
        }
    }


    public static void log( Throwable ex )
    {
        if ( ex != null )
        {
            String msg = ex.getMessage();
            if ( hasMessageCode( msg ) )
            {
                Logger.log( ex, msg );
            }
            else
            {
                Logger.log( ex, Messages.ERROR_XDIR_MAIN_UNKNOWN_EXCEPTION,msg );
            }
        }
    }
    
    public static void debug( String... msgs )
    {
        if ( debugEnabled )
        {
            writeMeg( Messages.STRING_LOGGER_LEVEL_DEBUG, msgs );
        }
    }


    public static void debug( Throwable ex, String... msgs )
    {
        if ( debugEnabled )
        {
            writeMeg( Messages.STRING_LOGGER_LEVEL_DEBUG, msgs );
            if ( ex != null )
            {
                 ex.printStackTrace( System.out );
            }
        }
    }


    public static String generateLogMeg( String level, String... msgs )
    {
        StringBuilder logStringBuilder = new StringBuilder( );
        if(debugEnabled){
            logStringBuilder.append( logDateFormat.format( new Date() ) );
            logStringBuilder.append( " " );
            logStringBuilder.append( level );
            logStringBuilder.append( " -" );
            logStringBuilder.append( Thread.currentThread().getName() );
            logStringBuilder.append( "-" );
        }else{
            logStringBuilder.append( level );
            logStringBuilder.append( " " );            
        }
        if ( msgs.length > 1 )
        {
            logStringBuilder.append( Messages.format( msgs ) );
        }
        else
        {
            logStringBuilder.append( msgs[0] );
        }
        return logStringBuilder.toString();
    }


    private static void writeMeg( String level, String... msgs )
    {
        String msg = generateLogMeg( level, msgs );
        outputMsg( msg );
    }


    private static synchronized void outputMsg( String logString )
    {
        if ( cachedMessage == null )
        {
            WriteToConsole( logString );
        }
        else
        {
            cachedMessage.add( logString );
        }
    }


    private static void WriteToConsole( String logString )
    {
        try
        {
            consoleDataOutput.writeBytes( logString + "\n" );
        }
        catch ( IOException neverHappens )
        {
            neverHappens.printStackTrace();
        }
    }


    private static void flushCachedMessage()
    {
        for ( String msg : cachedMessage )
        {
            WriteToConsole( msg );
        }
        cachedMessage = null;
    }


    private static void verifyLogsFolder( File logFile )
    {
        if ( !logFile.exists() )
        {
            if ( !logFile.getParentFile().exists() )
            {
                logFile.getParentFile().mkdirs();
                if ( Logger.isDebugEnabled() )
                    Logger.debug( "Created logs folder: " + logFile.getParentFile().getPath() );
            }
        }
    }

}
