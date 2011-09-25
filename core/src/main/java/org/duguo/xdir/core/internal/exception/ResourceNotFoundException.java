package org.duguo.xdir.core.internal.exception;

public class ResourceNotFoundException extends XdirException
{
    private static final long serialVersionUID = 0L;

    public ResourceNotFoundException(String resourceName){
        super( resourceName );
    }
}
