package org.duguo.xdir.http.json.impl.parser;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;

import org.duguo.xdir.http.json.Json;



/**
 * Part of the parse algorithm is come from @see org.apache.jackrabbit.commons.json.JsonParser
 * 
 */
public class JsonConverterUtil {

	protected static final int EOF = -1;
	private static final DefaultJsonTextConverter DEFAULT_JSON_CONVERTR= new DefaultJsonTextConverter();

    static {
        DEFAULT_JSON_CONVERTR.setIndentSpace(2);
    }

    
    public static String toText(Json json){
    	return DEFAULT_JSON_CONVERTR.serialize(json);
    }
    
    public static Json toJson(String jsonString){
    	try {
			return DEFAULT_JSON_CONVERTR.parse(jsonString);
		} catch (IOException e) {
			throw new RuntimeException("Failed to parse json\n"+jsonString,e);
		}
    }

	/**
	 * Escape string to match json requirement
	 * @param string
	 * @return
	 */
    public static String escape(String string) {
        if (string == null || string.length() == 0) {
            return "";
        }

        char         b;
        char         c = 0;
        int          i;
        int          len = string.length();
        StringBuffer sb = new StringBuffer(len + 4);
        String       t;

        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
            case '\\':
            case '"':
                sb.append('\\');
                sb.append(c);
                break;
            case '/':
                if (b == '<') {
                    sb.append('\\');
                }
                sb.append(c);
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\r':
                sb.append("\\r");
                break;
            default:
                if (c < ' ' || (c >= '\u0080' && c < '\u00a0') ||
                               (c >= '\u2000' && c < '\u2100')) {
                    t = "000" + Integer.toHexString(c);
                    sb.append("\\u" + t.substring(t.length() - 4));
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    /**
     * Return the characters up to the next close quote character.
     * Backslash processing is done. The formal JSON format does not
     * allow strings in single quotes, but an implementation is allowed to
     * accept them.
     *
     * @param r The reader.
     * @param quote The quoting character, either
     *      <code>"</code>&nbsp;<small>(double quote)</small> or
     *      <code>'</code>&nbsp;<small>(single quote)</small>.
     * @return      A String.
     * @throws IOException Unterminated string.
     */
	protected static String nextString(Reader r, char quote) throws IOException {
        int c;
        StringBuffer sb = new StringBuffer();
        for (;;) {
            c = r.read();
            switch (c) {
            case EOF:
            case '\n':
            case '\r':
                throw new RuntimeException("Invalid json data");
            case '\\':
                readEscapedChar(r, sb);
                break;
            default:
                if (c == quote) {
                    return sb.toString();
                }
                sb.append((char) c);
            }
        }
    }

	protected static int readSimpleValue(Reader r, StringBuilder valueBuilder,int next) throws IOException {
    	valueBuilder.append((char)next);
        for (;;) {
        	next = r.read();
        	if(next==EOF){
                throw new RuntimeException("Invalid json data");     		
        	}else if(next==',' || next==']' || next=='}'){
            	break;
            }
            valueBuilder.append((char)next);
        }
        return next;
    }

	protected static String readToEnd(Reader r) throws IOException {
        StringBuffer b = new StringBuffer();
        while (true) {
            int c = r.read();
            if (c ==EOF) {
                break;
            }
            b.append((char) c);
        }
        return b.toString();
    }

    /**
     * Get the next char in the string, skipping whitespace.
     *
     * @param reader The reader
     * @return A character, or -1 if there are no more characters.
     * @throws IOException If an error occurs.
     */
	protected static int readIgnoreWhitespace(Reader reader) throws IOException {
        int next;
        do {
            next = reader.read();
            if(next<0){
                throw new RuntimeException("Invalid json data");
            }
        } while (next == ' ' || next == '\n' || next == '\r' || next == '\t');
        return next;
    }


	protected static void readEscapedChar(Reader r, StringBuffer sb)
			throws IOException {
		int c;
		c = r.read();
		switch (c) {
		case 'b':
		    sb.append('\b');
		    break;
		case 't':
		    sb.append('\t');
		    break;
		case 'n':
		    sb.append('\n');
		    break;
		case 'f':
		    sb.append('\f');
		    break;
		case 'r':
		    sb.append('\r');
		    break;
		case 'u':
		    sb.append((char)Integer.parseInt(next(r, 4), 16));
		    break;
		case 'x' :
		    sb.append((char) Integer.parseInt(next(r, 2), 16));
		    break;
		default:
		    sb.append((char) c);
		}
	}

    private static String next(Reader r, int n) throws IOException {
        StringBuffer b = new StringBuffer(n);
        while (n-- > 0) {
            int c = r.read();
            if (c < 0) {
                throw new EOFException();
            }
            b.append((char) c);
        }
        return b.toString();
    }

}
