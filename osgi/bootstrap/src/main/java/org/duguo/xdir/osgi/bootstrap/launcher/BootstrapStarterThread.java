package org.duguo.xdir.osgi.bootstrap.launcher;

import org.duguo.xdir.osgi.bootstrap.i18n.Messages;
import org.duguo.xdir.osgi.bootstrap.log.Logger;
import org.duguo.xdir.osgi.bootstrap.provider.HandledException;
import org.duguo.xdir.osgi.bootstrap.provider.MessageHolderException;

public class BootstrapStarterThread extends AbstractBootstrapThread
{
    private static final String BOOTSTRAP_STARTER_NAME = "bootstrap-starter";
    

    private static final String KEY_XDIR_STATUS_BOOTSTRAP    = "xdir.status.bootstrap";
    private static final String VALUE_XDIR_STATUS_BOOTSTRAP_FAILED    = "failed";
    private static final String VALUE_XDIR_STATUS_BOOTSTRAP_SYSTEM    = "system";
    private static final String VALUE_XDIR_STATUS_BOOTSTRAP_USER      = "user";
    private static final String VALUE_XDIR_STATUS_BOOTSTRAP_STARTED   = "started";
    
    /**
     * 0  = STARTING
     * 1  = SUCCESS
     * -1 = FAILED
     */
    private int status=0;
    
    public BootstrapStarterThread(RuntimeContext runtimeContext){
        super( runtimeContext, BOOTSTRAP_STARTER_NAME );
    }
    
    public boolean isFailed(){
        return status==-1;
    }
    
    public boolean isSuccess(){
        return status==1;
    }
    
    public boolean isStarting(){
        return status==0;
    }

    @Override
    public synchronized void run()
    {
        startFramework();
        if(isFailed()){
            return;
        }
        
        startSystemBundles();
        if(isFailed()){
            return;
        }    
        startUserBundles();
        status=1;
    }

    private void startUserBundles()
    {
        try{
            runtimeContext.getConfiguration().put( KEY_XDIR_STATUS_BOOTSTRAP, VALUE_XDIR_STATUS_BOOTSTRAP_USER );    
            boolean success=runtimeContext.getRuntimeProvider().startUserBundles();
            if(success){
                runtimeContext.getBootstrapEventListener().onUserBundlesStarted();                
            }else{
                runtimeContext.getBootstrapEventListener().onUserBundlesFailed();       
                Logger.log(Messages.WARN_XDIR_RUNTIME_USER_FAILED);          
            }
            runtimeContext.getConfiguration().put( KEY_XDIR_STATUS_BOOTSTRAP, VALUE_XDIR_STATUS_BOOTSTRAP_STARTED );
        }catch(Throwable ex){
            runtimeContext.getBootstrapEventListener().onUserBundlesFailed();
            Logger.log(ex);
            Logger.log(Messages.WARN_XDIR_RUNTIME_USER_FAILED);
            Logger.showLogFileLocation();
        }
    }

    private void startSystemBundles()
    {
        try{
            runtimeContext.getConfiguration().put( KEY_XDIR_STATUS_BOOTSTRAP, VALUE_XDIR_STATUS_BOOTSTRAP_SYSTEM );
            runtimeContext.getRuntimeProvider().startSystemBundles();
            runtimeContext.getBootstrapEventListener().onSystemBundlesStarted();
        }catch(Throwable ex){
            runtimeContext.getConfiguration().put( KEY_XDIR_STATUS_BOOTSTRAP, VALUE_XDIR_STATUS_BOOTSTRAP_FAILED );
            if(ex instanceof MessageHolderException){
                Logger.log( ex.getMessage());
            }else if(!(ex instanceof HandledException)){
                Logger.log(ex);
            }
            Logger.log(Messages.ERROR_XDIR_RUNTIME_SYSTEM_FAILED);
            runtimeContext.getBootstrapEventListener().onSystemBundlesFailed();
            Logger.showLogFileLocation();
            status=-1;
        }
    }

    private void startFramework()
    {
        try{
            runtimeContext.getRuntimeProvider().startFramework();
            runtimeContext.getBootstrapEventListener().onRuntimeStarted();
        }catch(Throwable ex){
            if(ex instanceof MessageHolderException){
                Logger.log( ex.getMessage());
            }else if(!(ex instanceof HandledException)){
                Logger.log(ex);
            }
            Logger.log(Messages.ERROR_XDIR_RUNTIME_FRAMEWORK_FAILED);
            runtimeContext.getBootstrapEventListener().onRuntimeFailed();
            Logger.showLogFileLocation();
            status=-1;
        }
    }
}
