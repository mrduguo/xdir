package org.duguo.xdir.core.internal.server;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.gemini.blueprint.context.BundleContextAware;

public class OsgiFrameworkAdaptorImpl implements OsgiFrameworkAdaptor,BundleContextAware
{
    
    private static final Logger logger = LoggerFactory.getLogger( OsgiFrameworkAdaptorImpl.class );
    private static final String FRAMEWOR_INSTALL_STATUS="framework_install";
    
    private BundleContext bundleContext;
    private FrameworkListener bootstrapFrameworkListener;

    
    public synchronized String hotDeployFiles(String[] bundleFiles ) throws Exception
    {
        StringBuilder installCommand=new StringBuilder("install:");
        for(int i=0;i<bundleFiles.length;i++){
            if(i>0){
                installCommand.append(",");
            }
            installCommand.append( bundleFiles[i] );
        }
        sendCommand( installCommand.toString() );
        String status=null;
        
        long poolInterval = Long.parseLong( System.getProperty( "xdir.osgi.cmd.poll" ) );
        long waitCount = Long.parseLong( System.getProperty( "xdir.osgi.cmd.timeout" ) );
        waitCount = waitCount / poolInterval;
        while(waitCount>0){
            waitCount--;
            status=System.getProperty( FRAMEWOR_INSTALL_STATUS);
            if(status!=null){
                if("success".equals( status )){
                    return null;
                }else{
                    return status;
                }
            }
            Thread.sleep( poolInterval );
        }
        return "install bundles timeout";
    }


    public void restart()
    {
        sendCommand( "restart" );
    }


    public void sendCommand(String command)
    {
        Throwable codeInfoHolder = new RuntimeException( command );
        FrameworkEvent event = new FrameworkEvent( FrameworkEvent.INFO, getBundleContext().getBundle(), codeInfoHolder );
        bootstrapFrameworkListener.frameworkEvent( event );
        logger.info( "command [{}] sent to  bootstrapFrameworkListener",command );
    }


    public FrameworkListener getBootstrapFrameworkListener()
    {
        return bootstrapFrameworkListener;
    }


    public void setBootstrapFrameworkListener( FrameworkListener bootstrapFrameworkListener )
    {
        this.bootstrapFrameworkListener = bootstrapFrameworkListener;
    }


    public BundleContext getBundleContext()
    {
        return bundleContext;
    }


    public void setBundleContext( BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

}
