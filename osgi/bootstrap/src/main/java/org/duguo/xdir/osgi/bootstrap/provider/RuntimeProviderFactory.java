package org.duguo.xdir.osgi.bootstrap.provider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.osgi.framework.launch.FrameworkFactory;
import org.duguo.xdir.osgi.bootstrap.api.RuntimeProvider;
import org.duguo.xdir.osgi.bootstrap.i18n.Messages;
import org.duguo.xdir.osgi.bootstrap.launcher.RuntimeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RuntimeProviderFactory
{
    private static final Logger logger = LoggerFactory.getLogger(RuntimeProviderFactory.class);
    
    private static final String OSGI_CMD_IMPL_RUNTIME_FACTORY="runtime.factory";
    private static final String OSGI_CMD_IMPL_RUNTIME_PROVIDER="runtime.provider";

    private RuntimeContext runtimeContext;
    
    public RuntimeProviderFactory( RuntimeContext runtimeContext)
    {
        this.runtimeContext=runtimeContext;
    }
    
    public RuntimeProvider createOsgiProvider()
    {
        FrameworkFactory factory = createFrameworkFactory( );
        
        RuntimeProvider osgiProvider = createRuntimeProvider( );
        
        osgiProvider.createFramework(runtimeContext, factory);
        
        return osgiProvider;
    }

    @SuppressWarnings("unchecked")
    private FrameworkFactory createFrameworkFactory()
    {
        FrameworkFactory factory=null;
        String factoryClassName=retriveFrameworkFactory();
        try
        {
            Class factoryClass=FrameworkFactory.class.getClassLoader().loadClass(factoryClassName);
            factory = (FrameworkFactory) factoryClass.newInstance();
        }
        catch ( Throwable ex )
        {
            throw new RuntimeException(Messages.format( Messages.ERROR_XDIR_RUNTIME_FACTORY_INIT_FAILED,factoryClassName),ex);
        }
        return factory;
    }

    @SuppressWarnings("unchecked")
    private RuntimeProvider createRuntimeProvider()
    {
        RuntimeProvider osgiProvider=null;
        String providerClassName=retriveFrameworkProvider();
        try
        {
            Class providerClass=FrameworkFactory.class.getClassLoader().loadClass(providerClassName);
            osgiProvider = (RuntimeProvider) providerClass.newInstance();
        }
        catch ( Throwable ex )
        {
            throw new RuntimeException(Messages.format( Messages.ERROR_XDIR_RUNTIME_PROVIDER_INIT_FAILED,providerClassName),ex);
        }
        return osgiProvider;
    }


    private String retriveFrameworkProvider()
    {
        String providerClassName=runtimeContext.getConfiguration().getXdirOsgiCmdImpl(OSGI_CMD_IMPL_RUNTIME_PROVIDER);
        if(providerClassName==null){
            String factoryClassName=runtimeContext.getConfiguration().getXdirOsgiCmdImpl(OSGI_CMD_IMPL_RUNTIME_FACTORY);
            if(factoryClassName.indexOf( ".felix." )>0){
                providerClassName=FelixRuntimeProvider.class.getName();
            }else{
                logger.info(Messages.WARN_XDIR_RUNTIME_PROVIDER_DEFAULT, providerClassName);
                providerClassName=DefaultRuntimeProvider.class.getName();
            }
            runtimeContext.getConfiguration().setXdirOsgiCmdImpl( OSGI_CMD_IMPL_RUNTIME_PROVIDER,providerClassName );    
            if(logger.isDebugEnabled())
                logger.debug( "Framework provider class name ["+providerClassName+"] generated from factory class ["+factoryClassName+"]" );
        }else{
            if(logger.isDebugEnabled())
                logger.debug( "Framework provider class name ["+providerClassName+"] loaded from configuration" );
        }
        return providerClassName;
    }

    
    private String retriveFrameworkFactory()
    {
        String factoryClassName=runtimeContext.getConfiguration().getXdirOsgiCmdImpl(OSGI_CMD_IMPL_RUNTIME_FACTORY);
        if(factoryClassName==null){
            String factoryConfigurationSource="/META-INF/services/" + FrameworkFactory.class.getName();
            try{
                BufferedReader br=null;
                InputStream is = FrameworkFactory.class.getResourceAsStream(factoryConfigurationSource);
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                factoryClassName = br.readLine();
                br.close();
            }catch(Throwable ex){
                throw new RuntimeException(Messages.format( Messages.ERROR_XDIR_RUNTIME_FACTORY_NOT_FOUND,factoryConfigurationSource),ex);
            }
            runtimeContext.getConfiguration().setXdirOsgiCmdImpl( OSGI_CMD_IMPL_RUNTIME_FACTORY,factoryClassName );  
            if(logger.isDebugEnabled())
                logger.debug( "Framework factory class name ["+factoryClassName+"] loaded from resource ["+factoryConfigurationSource+"]" );
        }else{
            if(logger.isDebugEnabled())
                logger.debug( "Framework factory class name ["+factoryClassName+"] loaded from configuration" );
        }
        return factoryClassName;
    }
}
