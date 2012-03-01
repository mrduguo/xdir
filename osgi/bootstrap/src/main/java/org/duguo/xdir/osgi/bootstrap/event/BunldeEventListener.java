package org.duguo.xdir.osgi.bootstrap.event;

import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import org.duguo.xdir.osgi.bootstrap.provider.BundleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BunldeEventListener implements BundleListener
{
    private static final Logger logger = LoggerFactory.getLogger(BunldeEventListener.class);
    public static final int EVENT_MANUAL_INSTALLED=8888;
    
    private Set<Long> startingQueue=new HashSet<Long>();
    private Set<Long> failedBundles=new HashSet<Long>();
    private boolean lazyEnabled=false;
    
    
    public boolean isStarting(){
        int startingQueueSize=startingQueue.size();
        if(startingQueueSize>0){
            startingQueueSize=startingQueueSize-failedBundles.size();
        }
        return startingQueueSize>0;
    }
    public Set<Long> getStartingQueue(){
        return startingQueue;
    }
    public Set<Long> getFailureBundles(){
        return failedBundles;
    }
    
    public boolean hasFailure(){
        return failedBundles.size()>0;
    }
    
    public void resetAlllQueue(){
        startingQueue.clear();
        failedBundles.clear();
    }
    
    public void bundleChanged( BundleEvent event )
    {    if(logger.isDebugEnabled())
        logger.debug( BundleUtils.bundleDisplayString( event.getBundle()," event "+parseEventName(event.getType()) +" received: starting queue ["+startingQueue.size()+"] failed queue ["+failedBundles.size()+"]") );
    
    
        processEvent( event.getType(),event.getBundle() );
    }

    public synchronized void processEvent(int eventType, Bundle bundle )
    {
        switch ( eventType )
        {
            case EVENT_MANUAL_INSTALLED:
                startingQueue.add( bundle.getBundleId());
                if(BundleUtils.isFragment( bundle )){
                    failedBundles.add( bundle.getBundleId() );
                }
                break;
            case BundleEvent.LAZY_ACTIVATION:
                lazyEnabled=true;
                startingQueue.remove( bundle.getBundleId() );
                break;
            case BundleEvent.RESOLVED:
                if(BundleUtils.isFragment( bundle )){
                    startingQueue.remove( bundle.getBundleId() );
                    failedBundles.remove( bundle.getBundleId() );
                }
                break;
            case BundleEvent.STARTED:
                if(!lazyEnabled){
                    startingQueue.remove( bundle.getBundleId() );
                }
                break;
            case BundleEvent.STOPPED:
                failedBundles.add( bundle.getBundleId() );
                break;
            default:
                break;
        }
        if(logger.isDebugEnabled())
            logger.debug( BundleUtils.bundleDisplayString( bundle," event "+parseEventName(eventType) +" received: starting queue ["+startingQueue.size()+"] failed queue ["+failedBundles.size()+"]") );
        
    }
    


    private String parseEventName( int eventType )
    {
        switch ( eventType )
        {
            case BundleEvent.INSTALLED:
                return "INSTALLED";
            case BundleEvent.LAZY_ACTIVATION:
                return "LAZY_ACTIVATION";
            case BundleEvent.RESOLVED:
                return "RESOLVED";
            case BundleEvent.STARTED:
                return "STARTED";
            case BundleEvent.STARTING:
                return "STARTING";
            case BundleEvent.STOPPED:
                return "STOPPED";
            case BundleEvent.STOPPING:
                return "STOPPING";
            case BundleEvent.UNINSTALLED:
                return "UNINSTALLED";
            case BundleEvent.UNRESOLVED:
                return "UNRESOLVED";
            case BundleEvent.UPDATED:
                return "UNRESOLVED";
            case EVENT_MANUAL_INSTALLED:
                return "MANUAL_INSTALLED";
            default:
                break;
        }
        return "UNKNOWN EVENT "+eventType;
    }
}
