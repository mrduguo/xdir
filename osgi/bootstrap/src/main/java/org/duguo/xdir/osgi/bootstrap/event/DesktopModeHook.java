package org.duguo.xdir.osgi.bootstrap.event;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.osgi.framework.launch.Framework;
import org.duguo.xdir.osgi.spi.EventHandler;
import org.duguo.xdir.osgi.bootstrap.i18n.Messages;
import org.duguo.xdir.osgi.bootstrap.log.Logger;

public class DesktopModeHook implements EventHandler
{
    public void onEvent(Framework framework,String eventName){
        String httpPort=System.getProperty( "xdir.port.http");
        Logger.log( Messages.INFO_XDIR_EVENT_DESKTOP_MODE_ENABLED,httpPort );
        try{
          ServerSocket listener = new ServerSocket(Integer.parseInt( httpPort ));
          Socket server= listener.accept();
          Logger.debug( "Client start request received" );
          OutputStream serverOutputStream = server.getOutputStream();
          serverOutputStream.write( ("Server starting, <a href=\"?timestamp="+System.currentTimeMillis()+"\">Refresh later</a>").getBytes() );
          serverOutputStream.flush();
          serverOutputStream.close();
          server.close();
          Logger.debug( "Perform real server starting" );
        } catch (IOException ex) {
            Logger.log( ex, Messages.ERROR_XDIR_EVENT_DESKTOP_FAILED, httpPort );
        }
    }
}
