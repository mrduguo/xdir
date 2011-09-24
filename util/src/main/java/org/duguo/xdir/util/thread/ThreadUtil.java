package org.duguo.xdir.util.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadUtil
{
    
    private static final Logger logger = LoggerFactory.getLogger( ThreadUtil.class );
    
    public static void delayedAction(final long delayMilliseconds,final Action action){
       Thread delayedThread= new Thread(){
            public void run(){
                try{
                    if(logger.isDebugEnabled())
                        logger.debug("delayed action [{}] scheduled to run in [{}] milliseconds",action.getName(),delayMilliseconds);
                    Thread.sleep( delayMilliseconds );
                    if(logger.isDebugEnabled())
                        logger.debug("delayed action [{}] started",action.getName());
                    long timeSpent=System.currentTimeMillis();
                    action.execute();
                    timeSpent=System.currentTimeMillis()-timeSpent;
                    if(logger.isDebugEnabled())
                        logger.debug("delayed action [{}] finished in [{}] milliseconds",action.getName(),timeSpent);
                }catch(Exception ex){
                    logger.error( "delayed action ["+action.getName()+"] failed",ex );
                }
            }
        };
        delayedThread.setName( "delayed-action:"+action.getName() );
        delayedThread.start();        
    }
}
