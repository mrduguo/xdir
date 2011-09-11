package org.duguo.xdir.osgi.bootstrap.command;


import org.duguo.xdir.osgi.bootstrap.api.Command;
import org.duguo.xdir.osgi.bootstrap.conf.OsgiProperties;
import org.duguo.xdir.osgi.bootstrap.i18n.Messages;
import org.duguo.xdir.osgi.bootstrap.log.Logger;


public class CommandFactory
{
    @SuppressWarnings("unchecked")
    public Command createCommand( OsgiProperties configuration, String... args )
    {
        Command command = null;
        String commandStr = parseArgs( configuration, args );

        // we init logger if not start command.
        // for start command, we init inside the command
        if(!"start".equals( commandStr ))
            Logger.init( configuration);

        String cmdClassName = retriveCommandClass( configuration, commandStr );
        Class cmdClass = loadCommandClass( cmdClassName );
        if ( cmdClass != null )
        {
            command = createCommand( cmdClass );
            if ( command != null && Logger.isDebugEnabled() )
                Logger.debug( "Created command [" + commandStr + "] with args [" + reconstructArgs( args )+"]" );
        }
        else
        {
            Logger.log(Messages.ERROR_XDIR_CMD_UNKNOWN_COMMAND,commandStr ,reconstructArgs( args ) );
        }
        return command;
    }


    @SuppressWarnings("unchecked")
    private Class loadCommandClass( String cmdClassName )
    {
        Class commandClass = null;
        try
        {
            commandClass = CommandFactory.class.getClassLoader().loadClass( cmdClassName );
            if ( Logger.isDebugEnabled() )
                Logger.debug( "Command class [" + cmdClassName + "] loaded " );
        }
        catch ( ClassNotFoundException classNotFound )
        {
            if ( Logger.isDebugEnabled() )
                Logger.debug( "Command class [" + cmdClassName + "] not found" );
        }
        return commandClass;
    }


    @SuppressWarnings("unchecked")
    private Command createCommand( Class cmdClass )
    {
        Command command = null;
        try
        {
            command = ( Command ) cmdClass.newInstance();
            if ( Logger.isDebugEnabled() )
                Logger.debug( "Command instance [" + cmdClass.getName() + "] inited " );
        }
        catch ( Exception ex )
        {
            Logger.log( ex, Messages.ERROR_XDIR_CMD_INIT_FAILED,cmdClass.getName());
        }
        return command;
    }


    /**
     * Parse args from command line, sample values
     * -debug -console
     * start -debug -console
     * 
     * @param configuration
     * @param args
     * @return
     */
    private String parseArgs( OsgiProperties configuration, String... args )
    {
        String commandStr = null;
        if ( args.length > 0 )
        {
            for ( int i = 0; i < args.length; i++ )
            {
                String cmd = args[i].toLowerCase();
                if ( cmd.startsWith( "-" ) ) // arg start with - will mark as flag
                {
                    if ( "-debug".equals( cmd ) )
                    {
                        // already parsed in OsgiPropertiesFactory
                    }
                    else if ( "-console".equals( cmd ) )
                    {
                        if ( Logger.isDebugEnabled() )
                            Logger.debug( "Parsed [-console] from command line to enable console" );
                        configuration.putXdirOsgiConsole( "true" );
                    }
                    else if ( "-clean".equals( cmd ) )
                    {
                        if ( Logger.isDebugEnabled() )
                            Logger.debug( "Parsed [-clean] from command line to clean var folders" );
                        configuration.putXdirOsgiClean( "true" );
                    }
                    else
                    {
                        // sample trigger from command line: -testunsupporteflag
                        Logger.log( Messages.WARN_XDIR_CMD_UNSUPPORTED_FLAG, args[i] );
                    }
                }
                else // otherwise arg will assume as command
                {
                    if ( commandStr == null )
                    {
                        commandStr = cmd;
                    }
                    else
                    {
                        // sample trigger from command line [start stop]
                        Logger.log( Messages.WARN_XDIR_CMD_IGNORE_EXTRA_COMMAND, commandStr, args[i] );
                    }
                }
            }
        }
        if ( commandStr == null )
        {
            commandStr = "start";
        }
        if ( Logger.isDebugEnabled() )
            Logger.debug( "Parsed command [" + commandStr + "]" );
        return commandStr;
    }


    private String retriveCommandClass( OsgiProperties configuration, String cmdName )
    {
        String cmdClassName = configuration.getXdirOsgiCmdImpl( cmdName );
        if ( cmdClassName == null )
        {
            StringBuilder generatedClassName = new StringBuilder();
            generatedClassName.append( CommandFactory.class.getPackage().getName() );
            generatedClassName.append( "." );
            generatedClassName.append( cmdName.substring( 0, 1 ).toUpperCase() );
            if ( cmdName.length() > 1 )
            {
                generatedClassName.append( cmdName.substring( 1 ) );
            }
            generatedClassName.append( "Command" );

            cmdClassName = generatedClassName.toString();
            configuration.setXdirOsgiCmdImpl( cmdName, cmdClassName );
            if ( Logger.isDebugEnabled() )
                Logger.debug( "Built command class name " + cmdClassName + " from command name [" + cmdName + "]" );
        }
        else
        {
            if ( Logger.isDebugEnabled() )
                Logger.debug( "Retrived command class name " + cmdClassName + " from configuration" );
        }
        return cmdClassName;
    }


    private String reconstructArgs( String[] args )
    {
        StringBuilder sb = new StringBuilder();
        if ( args.length > 0 )
        {
            for ( String arg : args )
            {
                sb.append( " " );
                sb.append( arg );
            }
        }
        return sb.toString();
    }

}
