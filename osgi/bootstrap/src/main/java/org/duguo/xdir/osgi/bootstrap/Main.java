package org.duguo.xdir.osgi.bootstrap;


import org.duguo.xdir.osgi.bootstrap.api.Command;
import org.duguo.xdir.osgi.bootstrap.command.CommandFactory;
import org.duguo.xdir.osgi.bootstrap.conf.OsgiProperties;
import org.duguo.xdir.osgi.bootstrap.conf.OsgiPropertiesFactory;
import org.duguo.xdir.osgi.bootstrap.log.Logger;

/**
 * The Main class to boot the OSGi server
 * 
 *  
 * @author Guo Du
 *
 */
public class Main
{

    /**
     * @param args  command line args, sample values:
     *              start -debug -console
     *              stop
     *              status
     *              restart
     */
    public static void main( String... args )
    {
        
        // default status is 1 means fail, will get the final status from command execute method.
        int statusCode = 1;
        try
        {
            
            OsgiProperties configuration = loadOsgiProperties( args );
            
            Command command = createCommand( configuration, args );
            
            /*
             *  if command is null, means there are something wrong during 
             *  create command, and error message already written to console
             */
            if ( command != null )
            {
                statusCode = command.execute( configuration );
            }
        }
        catch ( Throwable ex )
        {
            Logger.log( ex);
        }
        
        if(Logger.isDebugEnabled())
            Logger.debug( "System.exit with status code ["+statusCode+"]" );
        // return the status to script
        System.exit( statusCode );
    }


    private static OsgiProperties loadOsgiProperties( String... args )
    {
        OsgiPropertiesFactory configurationFactory = new OsgiPropertiesFactory();
        OsgiProperties configuration = configurationFactory.createOsgiProperties( args );
        return configuration;
    }


    private static Command createCommand( OsgiProperties configuration, String... args )
    {
        CommandFactory commandFactory = new CommandFactory();
        Command command = commandFactory.createCommand( configuration, args );
        return command;
    }
}
