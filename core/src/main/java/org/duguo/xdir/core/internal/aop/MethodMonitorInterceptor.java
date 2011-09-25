package org.duguo.xdir.core.internal.aop;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.springframework.aop.interceptor.AbstractMonitoringInterceptor;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class MethodMonitorInterceptor extends AbstractMonitoringInterceptor
{

    private static final long serialVersionUID = 0L;

    @Override
    protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
        String name = createInvocationTraceName(invocation);
        Monitor monitor = MonitorFactory.start(name);
        try {
            return invocation.proceed();
        }
        finally {
            monitor.stop();
        }
    }

    @Override
    protected boolean isInterceptorEnabled(MethodInvocation invocation, Log logger) {
        return true;
    }
    
}