package org.duguo.xdir.osgi.bootstrap.log;


import java.io.DataOutput;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.duguo.xdir.osgi.bootstrap.command.FileUtils;
import org.duguo.xdir.osgi.bootstrap.i18n.Messages;


public class ConsoleDataOutput implements DataOutput
{

    private PrintStream rawSystemOut = System.out;
    protected File consoleLogFile;
    private boolean displaySystemout;

    public ConsoleDataOutput( File consoleLogFile, boolean displaySystemout)
    {
        this.consoleLogFile=consoleLogFile;
        this.displaySystemout=displaySystemout;
        replaceSystemOutAndErr();
    }
    
    public void systemOutPrint(String s){
        if(displaySystemout){
            writeToConsole( s );
        }
        writeToLogFile( s );
    }
    
    public void systemOutPrintln(String s){
        s=s+"\n";
        if(displaySystemout){
            writeToConsole( s );
        }
        s=Logger.generateLogMeg( Messages.STRING_LOGGER_LEVEL_STOUT, s );
        writeToLogFile( s );
        
    }
    
    public void systemErrPrint(String s){
        if(displaySystemout){
            writeToConsole( s );
        }
        writeToLogFile( s );
    }
    
    public void systemErrPrintln(String s){
        s=s+"\n";
        if(displaySystemout){
            writeToConsole( s );
        }
        s=Logger.generateLogMeg( Messages.STRING_LOGGER_LEVEL_STERR, s );
        writeToLogFile( s );
    }

    public void writeBytes( String s ) throws IOException
    {
        writeToConsole( s );
        writeToLogFile( s );
    }

    public void writeToLogFile( String s )
    {
        try
        {
            FileUtils.printToFile( consoleLogFile, s );
        }
        catch ( IOException e )
        {
            rawSystemOut.println("Failed to write log file ["+consoleLogFile.getPath()+"]:"+e.getMessage());
            e.printStackTrace(rawSystemOut);
        }
    }


    public void writeToConsole( String s )
    {
        rawSystemOut.print( s );
    }


    private void replaceSystemOutAndErr()
    {
        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream( consoleLogFile, true );
            PrintStream sysOut= new SystemOutPrintStream( this, fileOutputStream );
            PrintStream sysErr= new SystemErrPrintStream( this, fileOutputStream );
            System.setOut( sysOut );
            System.setErr( sysErr );
            fileOutputStream.close();
            if(Logger.isDebugEnabled()) Logger.debug( "Redirected System.out and System.err to logger" );
        }
        catch ( Exception neverHappens )
        {
            neverHappens.printStackTrace(rawSystemOut);
        }
    }

    /*************************************************
     * Unsupported methods
     *************************************************/



    private void unsupported()
    {
        throw new UnsupportedOperationException();
    }

    public void write( int b ) throws IOException
    {
        unsupported();
    }

    public void write( byte[] b ) throws IOException
    {
        unsupported();
    }


    public void write( byte[] b, int off, int len ) throws IOException
    {
        unsupported();
    }


    public void writeBoolean( boolean v ) throws IOException
    {
        unsupported();
    }


    public void writeByte( int v ) throws IOException
    {
        unsupported();
    }


    public void writeChar( int v ) throws IOException
    {
        unsupported();
    }


    public void writeChars( String s ) throws IOException
    {
        unsupported();
    }


    public void writeDouble( double v ) throws IOException
    {
        unsupported();
    }


    public void writeFloat( float v ) throws IOException
    {
        unsupported();
    }


    public void writeInt( int v ) throws IOException
    {
        unsupported();
    }


    public void writeLong( long v ) throws IOException
    {
        unsupported();
    }


    public void writeShort( int v ) throws IOException
    {
        unsupported();
    }


    public void writeUTF( String str ) throws IOException
    {
        unsupported();
    }

}
