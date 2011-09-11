package org.duguo.xdir.osgi.spi;

import org.osgi.framework.launch.Framework;

public interface EventHandler
{
   /**
    * 
    * @param framework      OSGi runtime framework instance
    * @param eventName      event name
    */
    void onEvent(Framework framework,String eventName);
    
}
