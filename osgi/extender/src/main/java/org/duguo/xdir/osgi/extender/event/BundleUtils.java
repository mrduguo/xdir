package org.duguo.xdir.osgi.extender.event;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;

public class BundleUtils
{

    public static void errorBundleEvent(Logger logger,BundleContext bundleContext,String message){
        logger.error( buildBundleMessage( bundleContext, message ));
    }
    
    public static void errorBundleEvent(Logger logger,BundleContext bundleContext,String message,Throwable ex){
        logger.error( buildBundleMessage( bundleContext, message ),ex);
    }
    
    public static void debugBundleEvent(Logger logger,BundleContext bundleContext,String message){
        if(logger.isDebugEnabled()){
            logger.debug( buildBundleMessage( bundleContext, message ));
        }
    }
    
    private static String buildBundleMessage( BundleContext bundleContext, String message )
    {
        StringBuilder sb=new StringBuilder();
        sb.append( "Bundle [");
        sb.append( bundleContext.getBundle().getBundleId() );
        sb.append( ":");
        sb.append( bundleContext.getBundle().getSymbolicName() );
        sb.append( ":");
        sb.append( bundleContext.getBundle().getVersion().toString() );
        sb.append( "] ");
        sb.append( message);
        return sb.toString();
    }
}
