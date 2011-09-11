package org.duguo.xdir.osgi.bootstrap.event;

import org.osgi.framework.launch.Framework;
import org.duguo.xdir.osgi.spi.EventHandler;

public class DisplayEventNameHook implements EventHandler
{
    public void onEvent(Framework framework,String eventName){
        System.out.println("Event triggered:"+eventName);
    }
}
