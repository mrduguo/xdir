package org.duguo.xdir.core.internal.jcr;

import javax.jcr.Session;

public interface SessionCallback
{
    public void execute(Session Session) throws Exception;
}
