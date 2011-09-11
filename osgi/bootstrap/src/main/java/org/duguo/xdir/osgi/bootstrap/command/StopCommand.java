package org.duguo.xdir.osgi.bootstrap.command;


import java.io.File;

import org.duguo.xdir.osgi.bootstrap.conf.OsgiProperties;
import org.duguo.xdir.osgi.bootstrap.i18n.Messages;
import org.duguo.xdir.osgi.bootstrap.log.Logger;


public class StopCommand extends AbstractCommand
{

    public int execute( OsgiProperties configuration ) throws Exception
    {
        setupConfiguration( configuration );

        int statusCode = 1;
        if ( isServerRunning() )
        {
            if ( allowedToStopServer() )
            {
                statusCode = sendStopCommand();
            }
        }
        else
        {
            Logger.log( Messages.INFO_XDIR_CMD_STOP_ALREADY );
            statusCode = 0;
        }
        return statusCode;
    }


    protected boolean allowedToStopServer()
    {
        if ( getConfiguration().isStopCommandEnabled() )
        {
            return true;
        }
        else
        {
            Logger.log( Messages.WARN_XDIR_CMD_STOP_DISABLED );
            return false;
        }
    }


    protected int sendStopCommand() throws Exception
    {
        int statusCode = 1;
        long requestId = System.currentTimeMillis();
        if ( sendCommandRequest( "stop", requestId ) )
        {
            if ( waitServerToStop( requestId ) )
            {
                statusCode = 0;
            }
        }
        else
        {
            Logger.log( Messages.ERROR_XDIR_CMD_STOP_SEND_FAILED );
        }
        return statusCode;
    }


    private boolean waitServerToStop( long requestId ) throws Exception
    {
        File commandInputFile = FileUtils.buildProcFile( getConfiguration(), "result.command." + requestId + ".input" );
        long i = getCommandTimeoutCount();
        while ( !commandInputFile.exists() )
        {
            i--;
            if ( i>0)
            {
                Thread.sleep( getCommandPollInterval() );
            }
            else
            {
                // stop time out
                Logger.log( Messages.ERROR_XDIR_CMD_STOP_TIMEOUT );
                return false;
            }
        }

        // stop success
        FileUtils.safelyDeleteFile( commandInputFile );
        Logger.log( Messages.INFO_XDIR_CMD_STOP_SUCCESS );
        return true;
    }

}
