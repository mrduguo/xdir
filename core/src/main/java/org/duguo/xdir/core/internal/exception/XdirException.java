package org.duguo.xdir.core.internal.exception;

public class XdirException extends RuntimeException
{
    private static final long serialVersionUID = 0L;

    public XdirException(String message){
        super( message );
    }

    public XdirException(String message,Throwable cause){
        super( message ,cause);
    }
}
