package org.duguo.xdir.osgi.bootstrap.log;


import java.io.FileOutputStream;


public class SystemOutPrintStream extends AbstractSystemPrintStream
{
    protected ConsoleDataOutput consoleDataOutput;

    public SystemOutPrintStream( ConsoleDataOutput consoleDataOutput, FileOutputStream fileOutputStream )
    {
        super( fileOutputStream );
        this.consoleDataOutput = consoleDataOutput;
    }


    @Override
    protected void doPrint( String s )
    {
        consoleDataOutput.systemOutPrint( s );
    }


    @Override
    protected void doPrintln( String s )
    {
        consoleDataOutput.systemOutPrintln( s );
    }
}
