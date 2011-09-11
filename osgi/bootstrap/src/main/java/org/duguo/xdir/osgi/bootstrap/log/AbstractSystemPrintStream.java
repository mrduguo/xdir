package org.duguo.xdir.osgi.bootstrap.log;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;


public abstract class AbstractSystemPrintStream extends PrintStream
{

    public AbstractSystemPrintStream( FileOutputStream fileOutputStream)
    {
        super( fileOutputStream );
    }
    
    protected abstract void doPrint(String s);
    protected abstract void doPrintln(String s);



    @Override
    public void print( String s )
    {
        doPrint( s );
    }

    @Override
    public void println( String s )
    {
        doPrintln( s );
    }


    @Override
    public void write( byte[] buf, int off, int len )
    {
        print( new String( buf, off, len ) );
    }


    @Override
    public void println( Object x )
    {
        if(x!=null){
            println( x.toString() );
        }
    }


    @Override
    public void flush()
    {
        // ignore flush
    }


    /*************************************************
     * Unsupported methods
     *************************************************/
    private void unsupported()
    {
        throw new UnsupportedOperationException();
    }


    @Override
    public PrintStream append( char c )
    {
        unsupported();
        return super.append( c );
    }


    @Override
    public PrintStream append( CharSequence csq, int start, int end )
    {
        unsupported();
        return super.append( csq, start, end );
    }


    @Override
    public PrintStream append( CharSequence csq )
    {
        unsupported();
        return super.append( csq );
    }


    @Override
    public boolean checkError()
    {
        unsupported();
        return super.checkError();
    }


    @Override
    protected void clearError()
    {
        unsupported();
        super.clearError();
    }


    @Override
    public void close()
    {
        unsupported();
        super.close();
    }


    @Override
    public PrintStream format( Locale l, String format, Object... args )
    {
        unsupported();
        return super.format( l, format, args );
    }


    @Override
    public PrintStream format( String format, Object... args )
    {
        unsupported();
        return super.format( format, args );
    }


    @Override
    public void print( boolean b )
    {
        unsupported();
        super.print( b );
    }


    @Override
    public void print( char c )
    {
        unsupported();
        super.print( c );
    }


    @Override
    public void print( char[] s )
    {
        unsupported();
        super.print( s );
    }


    @Override
    public void print( double d )
    {
        unsupported();
        super.print( d );
    }


    @Override
    public void print( float f )
    {
        unsupported();
        super.print( f );
    }


    @Override
    public void print( int i )
    {
        unsupported();
        super.print( i );
    }


    @Override
    public void print( long l )
    {
        unsupported();
        super.print( l );
    }


    @Override
    public void print( Object obj )
    {
        unsupported();
        super.print( obj );
    }


    @Override
    public PrintStream printf( Locale l, String format, Object... args )
    {
        unsupported();
        return super.printf( l, format, args );
    }


    @Override
    public PrintStream printf( String format, Object... args )
    {
        unsupported();
        return super.printf( format, args );
    }


    @Override
    public void println()
    {
        println( "" );
    }


    @Override
    public void println( boolean x )
    {
        println( ""+x );
    }


    @Override
    public void println( char x )
    {
        println( ""+x );
    }


    @Override
    public void println( char[] x )
    {
        println( ""+x.toString() );
    }


    @Override
    public void println( double x )
    {
        println( ""+x );
    }


    @Override
    public void println( float x )
    {
        println( ""+x );
    }


    @Override
    public void println( int x )
    {
        println( ""+x );
    }


    @Override
    public void println( long x )
    {
        println( ""+x );
    }


    @Override
    protected void setError()
    {
        unsupported();
        super.setError();
    }


    @Override
    public void write( int b )
    {
        unsupported();
        super.write( b );
    }


    @Override
    public void write( byte[] b ) throws IOException
    {
        unsupported();
        super.write( b );
    }

}
