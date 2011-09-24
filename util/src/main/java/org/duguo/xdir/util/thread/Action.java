package org.duguo.xdir.util.thread;

public interface Action
{
    public String getName();
    public void execute() throws Exception;
}
