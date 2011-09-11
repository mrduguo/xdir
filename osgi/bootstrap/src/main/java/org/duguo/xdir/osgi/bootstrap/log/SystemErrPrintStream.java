package org.duguo.xdir.osgi.bootstrap.log;


import java.io.FileOutputStream;


public class SystemErrPrintStream extends AbstractSystemPrintStream
{
    protected ConsoleDataOutput consoleDataOutput;

    public SystemErrPrintStream( ConsoleDataOutput consoleDataOutput, FileOutputStream fileOutputStream )
    {
        super( fileOutputStream );
        this.consoleDataOutput = consoleDataOutput;
    }


    @Override
    protected void doPrint( String s )
    {
        consoleDataOutput.systemErrPrint( s );
    }


    @Override
    protected void doPrintln( String s )
    {
        consoleDataOutput.systemErrPrintln( s );
    }
}
