package org.duguo.xdir.http.json.impl.parser;


import org.duguo.xdir.http.json.Json;

import java.io.IOException;
import java.io.Reader;



/**
 * Ideas from @see org.apache.jackrabbit.commons.json.JsonParser
 * 
 */
public class XmlConverterUtil {

    protected static final int EOF = -1;

    /** The table of entity values. It initially contains Character values for
     * amp, apos, gt, lt, quot.
     */
    public static final java.util.HashMap<String,Character> ESCAPE_ENTITIES;
    
    private static final XmlJsonTextConverter DEFAULT_XML_CONVERTR= new XmlJsonTextConverter();

    static {
        ESCAPE_ENTITIES = new java.util.HashMap<String,Character>(5);
        ESCAPE_ENTITIES.put("amp",  '&');
        ESCAPE_ENTITIES.put("gt",   '>');
        ESCAPE_ENTITIES.put("lt",   '<');
        ESCAPE_ENTITIES.put("quot", '"');
        DEFAULT_XML_CONVERTR.setIndentSpace(2);
    }
    
    public static String toXml(Json json){
    	return DEFAULT_XML_CONVERTR.serialize(json);
    }
    
    public static Json toJson(String xmlString){
    	try {
			return DEFAULT_XML_CONVERTR.parse(xmlString);
		} catch (IOException e) {
			throw new RuntimeException("Failed to parse xml\n"+xmlString,e);
		}
    }
 
    
    public static String escape(String string) {
        if (string == null || string.length() == 0) {
            return "";
        }

        char         c = 0;
        int          i;
        int          len = string.length();
        StringBuffer sb = new StringBuffer(len + 4);

        for (i = 0; i < len; i += 1) {
            c = string.charAt(i);
            switch (c) {
            case '"':
                sb.append("&quot;");
                break;
            case '<':
                sb.append("&lt;");
                break;
            case '>':
                sb.append("&gt;");
                break;
            case '&':
                sb.append("&amp;");
                break;
            default:
                sb.append(c);
            }
        }
        return sb.toString();
    }

	protected static int readIgnoreWhitespace(Reader reader) throws IOException {
        int next;
        do {
            next = reader.read();
            if(next<0){
                break;
            }
        } while (next == ' ' || next == '\n' || next == '\r' || next == '\t');
        return next;
    }
    
    /**
     * Get the next char in the string, skipping whitespace.
     *
     * @param reader The reader
     * @return A character, or -1 if there are no more characters.
     * @throws IOException If an error occurs.
     */
    protected static int readToNextTagStart(Reader reader) throws IOException {
        int next;
        do {
            next = reader.read();
            if(next<0){
            	return EOF;
            }
        } while (next != '<');
        next = reader.read();
        return next;
    }

    protected static int readToCurrentTagEnd(Reader reader,StringBuilder content) throws IOException {
        int next;
        do {
            next = reader.read();
            if(next<0){
            	return EOF;
            }else if(next=='>'){
                content.append((char)next);
            	break;
            }
            content.append((char)next);
        } while (true);
        return next;
    }

    protected static int readToNextChar(Reader reader,StringBuilder key,char searchChar) throws IOException {
        int next;
        do {
            next = reader.read();
            if(next<0){
            	return EOF;
            }else if(next==searchChar){
            	break;
            }
            key.append((char)next);
        } while (true);
        return next;
    }

    protected static int readKey(Reader reader,StringBuilder key) throws IOException {
        int next;
        do {
            next = reader.read();
            if(next<0){
            	return EOF;
            }else if(next==' ' || next=='/' || next=='>' || next=='=' || next=='\n'){
            	break;
            }
            key.append((char)next);
        } while (true);
        return next;
    }

    protected static int readText(Reader reader,StringBuilder text,int next) throws IOException {
        do {
            switch (next) {
            case EOF:
                throw new IOException("Unclosed text");
            case '&':
                text.append(nextEntity(reader,next));
                next = reader.read();
                continue;
            case '<':
                return next;
            default:
            }
            text.append((char)next);
            next = reader.read();
        } while (true);
    }
    
    protected static String readAttributeValue(Reader reader) throws IOException {
        int c;
        StringBuffer sb = new StringBuffer();
        for (;;) {
            c = reader.read();
            switch (c) {
            case EOF:
                throw new IOException("Unclosed attribute");
            case '&':
                sb.append(nextEntity(reader,c));
                continue;
            default:
                if (c == '\"') {
                    return sb.toString();
                }
                sb.append((char) c);
            }
        }
    }


    protected static String readCdata(Reader reader) throws IOException {
        StringBuffer text = new StringBuffer();
        int next;
        do {
            next = reader.read();
            switch (next) {
            case EOF:
                throw new IOException("Unclosed CDATA");
            case '&':
                text.append(nextEntity(reader,next));
                continue;
            case '>':
            	int textLength=text.length();
            	if(text.charAt(textLength-2)==']' && text.charAt(textLength-1)==']'){
            		text.delete(textLength-2, textLength);
            		text.delete(0, 6);
                    return text.toString();
            	}
                break;
            default:
            }
            text.append((char)next);
        } while (true);
    }


    protected static String readComment(Reader reader,StringBuilder comment) throws IOException {
        int next;
        do {
            next = reader.read();
            if(next<0){
                throw new IOException("Unclosed Comment");
            }else if(next=='>'){
            	int textLength=comment.length();
            	if(comment.charAt(textLength-2)=='-' && comment.charAt(textLength-1)=='-'){
            		comment.append(">");
                	break;	
            	}
            }
            comment.append((char)next);
        } while (true);
        return comment.toString();
    }   

    /**
     * Return the next entity. These entities are translated to Characters:
     *     <code>&amp;  &apos;  &gt;  &lt;  &quot;</code>.
     * @param a An ampersand character.
     * @return  A Character or an entity String if the entity is not recognized.
     * @throws JSONException If missing ';' in XML entity.
     */
    private static Object nextEntity(Reader reader,int a) throws IOException {
        StringBuffer sb = new StringBuffer();
        int next;
        for (;;) {
        	next = reader.read();
            if (Character.isLetterOrDigit((char)next) || next == '#') {
                sb.append(Character.toLowerCase((char)next));
            } else if (next == ';') {
                break;
            } else {
                throw new IOException("Missing ';' in XML entity: &" + sb);
            }
        }
        String s = sb.toString();
        Object e = ESCAPE_ENTITIES.get(s);
        return e != null ? e : (char)a + s + ";";
    }

}
