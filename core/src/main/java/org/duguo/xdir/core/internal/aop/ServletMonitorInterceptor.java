package org.duguo.xdir.core.internal.aop;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.springframework.aop.interceptor.AbstractMonitoringInterceptor;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class ServletMonitorInterceptor extends AbstractMonitoringInterceptor
{

    private static final long serialVersionUID = 0L;
    
    private Method statusMethod;

    @Override
    protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
        String name =null;
        if(invocation.getArguments().length>0){
            Object requestArg=invocation.getArguments()[0];
            if(requestArg instanceof HttpServletRequest){
                name= ((HttpServletRequest)requestArg).getRequestURL().toString();
                RequestUrlHolder.set( name );
                name="/"+name;
            }
        }
        if(name==null){
            name = createInvocationTraceName(invocation);
        }
        
        Monitor monitor = MonitorFactory.start(name);
        int status=0;
        try {
            Object result=invocation.proceed();
            if(statusMethod==null){
                if(invocation.getArguments().length>1){
                    Object responseArg=invocation.getArguments()[1];
                    try{
                        statusMethod=responseArg.getClass().getMethod( "getStatus", new Class[0] );
                    }catch(NoSuchMethodException ex){
                        logger.error( "Unknown HttpServletResponse implementation ["+responseArg.getClass().getName()+"]" );
                    }
                }
            }
            return result;
        }
        finally {
            monitor.stop();
            if(statusMethod!=null){
                status=(Integer)statusMethod.invoke( invocation.getArguments()[1], new Object[0] );
                MonitorFactory.add( "HTTP_STATUS_"+status, "ms.", monitor.getLastValue() );
            }
            if(status>=400){
                MonitorFactory.remove( name, "" );
            }
        }
    }

    @Override
    protected boolean isInterceptorEnabled(MethodInvocation invocation, Log logger) {
        return true;
    }
    
}
