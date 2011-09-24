package org.duguo.xdir.jcr.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


/**
 * Messages loader to support utf-8 encoded message. Also the key splitter will only be "=", ":" could be part of key 
 * 
 * @see java.util.Properties.load(InputStream inStream) 
 * @author duguo
 *
 */
public class MessagesLoader
{

    public static Map<String, String> load( String messagesFile )
    {
        Map<String, String> messages = new HashMap<String, String>();
        load( messages, messagesFile );
        return messages;
    }


    public static void load( Map<String, String> messages, String messagesFile )
    {
        try
        {
            loadMessages( messages, messagesFile );
        }
        catch ( RuntimeException ex )
        {
            throw ex;
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "failed to load messages from [" + messagesFile + "]", ex );
        }
    }


    /**
     * NOTICE: following are a modified version of 
     * @java.util.Properties.load(InputStream inStream) 
     * to load message from utf-8 encode message file in to a map
     */
    private static void loadMessages( Map<String, String> messages, String messagesFile ) throws Exception
    {
        File messagesInputFile = new File( messagesFile );
        if ( !messagesInputFile.exists() || !messagesInputFile.isFile() )
        {
            return;
        }
        FileInputStream fileInputStream = new FileInputStream( messagesInputFile );
        InputStreamReader inputStreamReader = new InputStreamReader( fileInputStream, System.getProperty( "xdir.file.encoding","utf-8") );
        char[] convtBuf = new char[1024];
        LineReader lr = new LineReader( inputStreamReader );

        int limit;
        int keyLen;
        int valueStart;
        char c;
        boolean hasSep;
        boolean precedingBackslash;

        while ( ( limit = lr.readLine() ) >= 0 )
        {
            c = 0;
            keyLen = 0;
            valueStart = limit;
            hasSep = false;

            //System.out.println("line=<" + new String(lineBuf, 0, limit) + ">");
            precedingBackslash = false;
            while ( keyLen < limit )
            {
                c = lr.lineBuf[keyLen];
                //need check if escaped.
                if ( ( c == '=' ) && !precedingBackslash )
                {
                    valueStart = keyLen + 1;
                    hasSep = true;
                    break;
                }
                else if ( ( c == ' ' || c == '\t' || c == '\f' ) && !precedingBackslash )
                {
                    valueStart = keyLen + 1;
                    break;
                }
                if ( c == '\\' )
                {
                    precedingBackslash = !precedingBackslash;
                }
                else
                {
                    precedingBackslash = false;
                }
                keyLen++;
            }
            while ( valueStart < limit )
            {
                c = lr.lineBuf[valueStart];
                if ( c != ' ' && c != '\t' && c != '\f' )
                {
                    if ( !hasSep && ( c == '=' ) )
                    {
                        hasSep = true;
                    }
                    else
                    {
                        break;
                    }
                }
                valueStart++;
            }
            String key = loadConvert( lr.lineBuf, 0, keyLen, convtBuf );
            String value = loadConvert( lr.lineBuf, valueStart, limit - valueStart, convtBuf );
            messages.put( key, value );
        }
    }

    static class LineReader
    {
        public LineReader( InputStreamReader inputStreamReader )
        {
            this.inputStreamReader = inputStreamReader;
        }

        char[] inBuf = new char[8192];
        char[] lineBuf = new char[1024];
        int inLimit = 0;
        int inOff = 0;
        InputStreamReader inputStreamReader;


        int readLine() throws IOException
        {
            int len = 0;
            char c = 0;

            boolean skipWhiteSpace = true;
            boolean isCommentLine = false;
            boolean isNewLine = true;
            boolean appendedLineBegin = false;
            boolean precedingBackslash = false;
            boolean skipLF = false;

            while ( true )
            {
                if ( inOff >= inLimit )
                {
                    inLimit = inputStreamReader.read( inBuf );
                    inOff = 0;
                    if ( inLimit <= 0 )
                    {
                        if ( len == 0 || isCommentLine )
                        {
                            return -1;
                        }
                        return len;
                    }
                }
                //The line below is equivalent to calling a 
                //ISO8859-1 decoder.
                c = inBuf[inOff++];
                if ( skipLF )
                {
                    skipLF = false;
                    if ( c == '\n' )
                    {
                        continue;
                    }
                }
                if ( skipWhiteSpace )
                {
                    if ( c == ' ' || c == '\t' || c == '\f' )
                    {
                        continue;
                    }
                    if ( !appendedLineBegin && ( c == '\r' || c == '\n' ) )
                    {
                        continue;
                    }
                    skipWhiteSpace = false;
                    appendedLineBegin = false;
                }
                if ( isNewLine )
                {
                    isNewLine = false;
                    if ( c == '#' || c == '!' )
                    {
                        isCommentLine = true;
                        continue;
                    }
                }

                if ( c != '\n' && c != '\r' )
                {
                    lineBuf[len++] = c;
                    if ( len == lineBuf.length )
                    {
                        int newLength = lineBuf.length * 2;
                        if ( newLength < 0 )
                        {
                            newLength = Integer.MAX_VALUE;
                        }
                        char[] buf = new char[newLength];
                        System.arraycopy( lineBuf, 0, buf, 0, lineBuf.length );
                        lineBuf = buf;
                    }
                    //flip the preceding backslash flag
                    if ( c == '\\' )
                    {
                        precedingBackslash = !precedingBackslash;
                    }
                    else
                    {
                        precedingBackslash = false;
                    }
                }
                else
                {
                    // reached EOL
                    if ( isCommentLine || len == 0 )
                    {
                        isCommentLine = false;
                        isNewLine = true;
                        skipWhiteSpace = true;
                        len = 0;
                        continue;
                    }
                    if ( inOff >= inLimit )
                    {
                        inLimit = inputStreamReader.read( inBuf );
                        inOff = 0;
                        if ( inLimit <= 0 )
                        {
                            return len;
                        }
                    }
                    if ( precedingBackslash )
                    {
                        len -= 1;
                        //skip the leading whitespace characters in following line
                        skipWhiteSpace = true;
                        appendedLineBegin = true;
                        precedingBackslash = false;
                        if ( c == '\r' )
                        {
                            skipLF = true;
                        }
                    }
                    else
                    {
                        return len;
                    }
                }
            }
        }
    }


    private static String loadConvert( char[] in, int off, int len, char[] convtBuf )
    {
        if ( convtBuf.length < len )
        {
            int newLen = len * 2;
            if ( newLen < 0 )
            {
                newLen = Integer.MAX_VALUE;
            }
            convtBuf = new char[newLen];
        }
        char aChar;
        char[] out = convtBuf;
        int outLen = 0;
        int end = off + len;

        while ( off < end )
        {
            aChar = in[off++];
            if ( aChar == '\\' )
            {
                aChar = in[off++];
                if ( aChar == 't' )
                    aChar = '\t';
                else if ( aChar == 'r' )
                    aChar = '\r';
                else if ( aChar == 'n' )
                    aChar = '\n';
                else if ( aChar == 'f' )
                    aChar = '\f';
                out[outLen++] = aChar;
            }
            else
            {
                out[outLen++] = ( char ) aChar;
            }
        }
        return new String( out, 0, outLen );
    }

}
