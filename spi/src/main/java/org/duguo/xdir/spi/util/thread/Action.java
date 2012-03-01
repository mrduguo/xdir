package org.duguo.xdir.spi.util.thread;

public interface Action
{
    public String getName();
    public void execute() throws Exception;
}
