package org.duguo.xdir.core.internal.aop;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.springframework.aop.interceptor.SimpleTraceInterceptor;

public class DebugInterceptor extends SimpleTraceInterceptor
{

    private static final long serialVersionUID = 0L;
    
    @Override
    protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
        String invocationDescription = getInvocationDescription(invocation);
        logger.debug("> " + invocationDescription);
        try {
            Object rval = invocation.proceed();
            logger.debug("< " + invocationDescription);
            return rval;
        }
        catch (Throwable ex) {
            logger.debug("Exception thrown in " + invocationDescription, ex);
            throw ex;
        }
    }

    @Override
    protected boolean isInterceptorEnabled(MethodInvocation invocation, Log logger) {
        return logger.isDebugEnabled();
    }
}
