package org.duguo.xdir.osgi.bootstrap.command;


import org.duguo.xdir.osgi.bootstrap.conf.OsgiProperties;
import org.duguo.xdir.osgi.bootstrap.i18n.Messages;
import org.duguo.xdir.osgi.bootstrap.log.Logger;


public class RestartCommand extends StopCommand
{


    public int execute(OsgiProperties configuration) throws Exception
    {
        setupConfiguration( configuration );
        
        int statusCode=1;
        if ( isServerRunning(  ) )
        {
            if(allowedToStopServer()){
                statusCode = sendRestartCommand(  );
            }
        }else{
            Logger.log( Messages.WARN_XDIR_CMD_RESTART_STOPPED );
        }
        return statusCode;
    }

    private int sendRestartCommand() throws Exception
    {

        createRestartFile(  );
     
        int statusCode = sendStopCommand( );
        if(statusCode==0){
            Logger.log( Messages.INFO_XDIR_CMD_RESTART_SUCCESS );
        }else{
            Logger.log( Messages.ERROR_XDIR_CMD_RESTART_FAILED );
        }
        return statusCode;
    }
}
