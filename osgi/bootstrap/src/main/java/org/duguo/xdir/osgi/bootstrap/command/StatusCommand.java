package org.duguo.xdir.osgi.bootstrap.command;


import org.duguo.xdir.osgi.bootstrap.conf.OsgiProperties;
import org.duguo.xdir.osgi.bootstrap.i18n.Messages;
import org.duguo.xdir.osgi.bootstrap.log.Logger;


public class StatusCommand extends AbstractCommand
{


    public int execute(OsgiProperties configuration) throws Exception
    {
        setupConfiguration( configuration );
        
        if ( isServerRunning(  ) )
        {
            Logger.log( Messages.INFO_XDIR_CMD_STATUS_RUNNING );
        }
        else
        {
            Logger.log( Messages.INFO_XDIR_CMD_STATUS_STOPPED );
        }
        return 0;
    }

}
