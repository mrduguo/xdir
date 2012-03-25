package org.duguo.xdir.osgi.bootstrap.osgiserver;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BunldeEventListener implements BundleListener
{
    private static final Logger logger = LoggerFactory.getLogger(BunldeEventListener.class);

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
    
    public void bundleChanged( BundleEvent event )
    {
        processEvent( event.getType(),event.getBundle() );
    }

    public synchronized void processEvent(int eventType, Bundle bundle )
    {
        if(logger.isDebugEnabled()) {
            logger.debug( BundleUtils.bundleDisplayString( bundle," event "+parseEventName(eventType) +" received: starting queue ["+startingQueue.size()+"] failed queue ["+failedBundles.size()+"]") );
        }
        switch ( eventType )
        {
            case BundleEvent.INSTALLED:
                startingQueue.add(bundle.getBundleId());
                break;
            case BundleEvent.LAZY_ACTIVATION:
                lazyEnabled=true;
                bundleStartedSuccessfully(bundle);
                break;
            case BundleEvent.RESOLVED:
                if(BundleUtils.isFragment( bundle )){
                    if(logger.isDebugEnabled()){ 
                        logger.debug(BundleUtils.bundleDisplayString(bundle, "Fragment bundle started"));
                    }
                    bundleStartedSuccessfully(bundle);
                }
                break;
            case BundleEvent.STARTED:
                if(!lazyEnabled){
                    bundleStartedSuccessfully(bundle);
                }
                break;
            case BundleEvent.STOPPED:
                failedBundles.add( bundle.getBundleId() );
                break;
            default:
                break;
        }
    }

    private void bundleStartedSuccessfully(Bundle bundle) {
        startingQueue.remove( bundle.getBundleId() );
        if(logger.isDebugEnabled()) {
            displayPackageHeader(bundle, Constants.EXPORT_PACKAGE);
            displayPackageHeader(bundle, Constants.IMPORT_PACKAGE);
            displayPackageHeader(bundle, Constants.DYNAMICIMPORT_PACKAGE);
        }
    }

    private void displayPackageHeader(Bundle bundle, String packageHeader) {
        String packages = (String)bundle.getHeaders().get(packageHeader);
        if(packages!=null){
            Map<String, Map> packagesEntries=BundleUtils.parsePackage(packages);
            StringBuilder headerLogMsg=new StringBuilder(BundleUtils.bundleDisplayString(bundle, packageHeader));
            for(Map.Entry<String,Map> entry:packagesEntries.entrySet()){
                headerLogMsg.append("\n");
                headerLogMsg.append(entry.getKey());
                for(Object attrName:entry.getValue().keySet()){
                    headerLogMsg.append(";");
                    headerLogMsg.append(attrName);
                    headerLogMsg.append("=");
                    Object value = entry.getValue().get(attrName);
                    if(value instanceof String[]){
                        String[] arrayValue = (String[]) value;
                        for(int i=0;i<arrayValue.length;i++) {
                            if(i>0)
                                headerLogMsg.append(",");
                            headerLogMsg.append(arrayValue[i]);
                        }
                        
                    }else{
                        headerLogMsg.append(value);
                    }
                }
            }
            logger.debug(headerLogMsg.toString());
        }
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
            default:
                break;
        }
        return "UNKNOWN EVENT "+eventType;
    }
}
