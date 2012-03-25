package org.duguo.xdir.core.internal.template.freemarker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

public class BufferedResponse implements HttpServletResponse
{
    
    private static final Logger logger = LoggerFactory.getLogger( BufferedResponse.class );
    
    private HttpServletResponse rawResponse;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    public BufferedResponse( HttpServletResponse rawResponse )
    {
        this.rawResponse = rawResponse;
    }

    public HttpServletResponse getRawResponse()
    {
        return rawResponse;
    }
    
    /***************************************************
     * override methods
     ***************************************************/
    
    public PrintWriter getWriter() throws IOException
    {    
        if(stringWriter==null){
            stringWriter=new StringWriter();
            printWriter=new PrintWriter(stringWriter);
        }
        return printWriter;
    }

    public void flushBuffer() throws IOException
    {
        if(stringWriter!=null && stringWriter.getBuffer().length()>0){
            if(!rawResponse.isCommitted()){
                rawResponse.getWriter().write( stringWriter.toString() );
                stringWriter=null;                
            }else if(logger.isDebugEnabled()){
                    logger.debug("response already committed, ignore the buffered output\n{}",stringWriter.toString()); 
            }
        }
        rawResponse.flushBuffer();
    }
    
    /***************************************************
     * delegated methods
     ***************************************************/
    
    public void addCookie( Cookie cookie )
    {
        rawResponse.addCookie( cookie );
    }
    public void addDateHeader( String name, long date )
    {
        rawResponse.addDateHeader( name, date );
    }
    public void addHeader( String name, String value )
    {
        rawResponse.addHeader( name, value );
    }
    public void addIntHeader( String name, int value )
    {
        rawResponse.addIntHeader( name, value );
    }
    public boolean containsHeader( String name )
    {
        return rawResponse.containsHeader( name );
    }
    
    /**
     * @deprecated
     */
    public String encodeRedirectUrl( String url )
    {
        return rawResponse.encodeRedirectUrl( url );
    }
    public String encodeRedirectURL( String url )
    {
        return rawResponse.encodeRedirectURL( url );
    }
    
    /**
     * @deprecated
     */
    public String encodeUrl( String url )
    {
        return rawResponse.encodeUrl( url );
    }
    public String encodeURL( String url )
    {
        return rawResponse.encodeURL( url );
    }
    public int getBufferSize()
    {
        return rawResponse.getBufferSize();
    }
    public String getCharacterEncoding()
    {
        return rawResponse.getCharacterEncoding();
    }
    public String getContentType()
    {
        return rawResponse.getContentType();
    }
    public Locale getLocale()
    {
        return rawResponse.getLocale();
    }
    public ServletOutputStream getOutputStream() throws IOException
    {
        return rawResponse.getOutputStream();
    }
    public boolean isCommitted()
    {
        return rawResponse.isCommitted();
    }
    public void reset()
    {
        rawResponse.reset();
    }
    public void resetBuffer()
    {
        rawResponse.resetBuffer();
    }
    public void sendError( int sc, String msg ) throws IOException
    {
        rawResponse.sendError( sc, msg );
    }
    public void sendError( int sc ) throws IOException
    {
        rawResponse.sendError( sc );
    }
    public void sendRedirect( String location ) throws IOException
    {
        rawResponse.sendRedirect( location );
    }
    public void setBufferSize( int size )
    {
        rawResponse.setBufferSize( size );
    }
    public void setCharacterEncoding( String charset )
    {
        rawResponse.setCharacterEncoding( charset );
    }
    public void setContentLength( int len )
    {
        rawResponse.setContentLength( len );
    }
    public void setContentType( String type )
    {
        rawResponse.setContentType( type );
    }
    public void setDateHeader( String name, long date )
    {
        rawResponse.setDateHeader( name, date );
    }
    public void setHeader( String name, String value )
    {
        rawResponse.setHeader( name, value );
    }
    public void setIntHeader( String name, int value )
    {
        rawResponse.setIntHeader( name, value );
    }
    public void setLocale( Locale loc )
    {
        rawResponse.setLocale( loc );
    }
    
    /**
     * @deprecated
     */
    public void setStatus( int sc, String sm )
    {
        rawResponse.setStatus( sc, sm );
    }
    public void setStatus( int sc )
    {
        rawResponse.setStatus( sc );
    }
}
