package org.duguo.xdir.jcr.utils;

import java.io.*;
import java.util.*;

/**
 * A clone of {@link java.util.Properties} to support utf-8 encoded properties
 *
 * Changes:
 *
 *  1. sorted key by natural order of key
 *  2. doesn't escape :
 *  3. use utf-8 for encoding
 *  4. doesn't support xml load/save
 *  5. doesn't skip white space at new line
 *  6. doesn't escape tab
 *  7. doesn't escape =
 *  8. keep a line break with ending slash
 *
 */
public class Utf8SortedProperties extends TreeMap<String,String> {
    

    /**
     * Creates an empty property list with no default values.
     */
    public Utf8SortedProperties() {
    }

    /**
     * Calls the <tt>Hashtable</tt> method <code>put</code>. Provided for
     * parallelism with the <tt>getProperty</tt> method. Enforces use of
     * strings for property keys and values. The value returned is the
     * result of the <tt>Hashtable</tt> call to <code>put</code>.
     *
     * @param key the key to be placed into this property list.
     * @param value the value corresponding to <tt>key</tt>.
     * @return     the previous value of the specified key in this property
     *             list, or <code>null</code> if it did not have one.
     * @see #getProperty
     * @since    1.2
     */
    public synchronized Object setProperty(String key, String value) {
        return put(key, value);
    }


    /**
     * Reads a property list (key and element pairs) from the input
     * character stream in a simple line-oriented format.
     * <p>
     * Properties are processed in terms of lines. There are two
     * kinds of line, <i>natural lines</i> and <i>logical lines</i>.
     * A natural line is defined as a line of
     * characters that is terminated either by a set of line terminator
     * characters (<code>\n</code> or <code>\r</code> or <code>\r\n</code>)
     * or by the end of the stream. A natural line may be either a blank line,
     * a comment line, or hold all or some of a key-element pair. A logical
     * line holds all the data of a key-element pair, which may be spread
     * out across several adjacent natural lines by escaping
     * the line terminator sequence with a backslash character
     * <code>\</code>.  Note that a comment line cannot be extended
     * in this manner; every natural line that is a comment must have
     * its own comment indicator, as described below. Lines are read from
     * input until the end of the stream is reached.
     *
     * <p>
     * A natural line that contains only white space characters is
     * considered blank and is ignored.  A comment line has an ASCII
     * <code>'#'</code> or <code>'!'</code> as its first non-white
     * space character; comment lines are also ignored and do not
     * encode key-element information.  In addition to line
     * terminators, this format considers the characters space
     * (<code>' '</code>, <code>'&#92;u0020'</code>), tab
     * (<code>'\t'</code>, <code>'&#92;u0009'</code>), and form feed
     * (<code>'\f'</code>, <code>'&#92;u000C'</code>) to be white
     * space.
     *
     * <p>
     * If a logical line is spread across several natural lines, the
     * backslash escaping the line terminator sequence, the line
     * terminator sequence, and any white space at the start of the
     * following line have no affect on the key or element values.
     * The remainder of the discussion of key and element parsing
     * (when loading) will assume all the characters constituting
     * the key and element appear on a single natural line after
     * line continuation characters have been removed.  Note that
     * it is <i>not</i> sufficient to only examine the character
     * preceding a line terminator sequence to decide if the line
     * terminator is escaped; there must be an odd number of
     * contiguous backslashes for the line terminator to be escaped.
     * Since the input is processed from left to right, a
     * non-zero even number of 2<i>n</i> contiguous backslashes
     * before a line terminator (or elsewhere) encodes <i>n</i>
     * backslashes after escape processing.
     *
     * <p>
     * The key contains all of the characters in the line starting
     * with the first non-white space character and up to, but not
     * including, the first unescaped <code>'='</code>,
     * <code>':'</code>, or white space character other than a line
     * terminator. All of these key termination characters may be
     * included in the key by escaping them with a preceding backslash
     * character; for example,<p>
     *
     * <code>\:\=</code><p>
     *
     * would be the two-character key <code>":="</code>.  Line
     * terminator characters can be included using <code>\r</code> and
     * <code>\n</code> escape sequences.  Any white space after the
     * key is skipped; if the first non-white space character after
     * the key is <code>'='</code> or <code>':'</code>, then it is
     * ignored and any white space characters after it are also
     * skipped.  All remaining characters on the line become part of
     * the associated element string; if there are no remaining
     * characters, the element is the empty string
     * <code>&quot;&quot;</code>.  Once the raw character sequences
     * constituting the key and element are identified, escape
     * processing is performed as described above.
     *
     * <p>
     * As an example, each of the following three lines specifies the key
     * <code>"Truth"</code> and the associated element value
     * <code>"Beauty"</code>:
     * <p>
     * <pre>
     * Truth = Beauty
     *	Truth:Beauty
     * Truth			:Beauty
     * </pre>
     * As another example, the following three lines specify a single
     * property:
     * <p>
     * <pre>
     * fruits                           apple, banana, pear, \
     *                                  cantaloupe, watermelon, \
     *                                  kiwi, mango
     * </pre>
     * The key is <code>"fruits"</code> and the associated element is:
     * <p>
     * <pre>"apple, banana, pear, cantaloupe, watermelon, kiwi, mango"</pre>
     * Note that a space appears before each <code>\</code> so that a space
     * will appear after each comma in the final result; the <code>\</code>,
     * line terminator, and leading white space on the continuation line are
     * merely discarded and are <i>not</i> replaced by one or more other
     * characters.
     * <p>
     * As a third example, the line:
     * <p>
     * <pre>cheeses
     * </pre>
     * specifies that the key is <code>"cheeses"</code> and the associated
     * element is the empty string <code>""</code>.<p>
     * <p>
     *
     * <a name="unicodeescapes"></a>
     * Characters in keys and elements can be represented in escape
     * sequences similar to those used for character and string literals
     * (see <a
     * href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.3">&sect;3.3</a>
     * and <a
     * href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10.6">&sect;3.10.6</a>
     * of the <i>Java Language Specification</i>).
     *
     * The differences from the character escape sequences and Unicode
     * escapes used for characters and strings are:
     *
     * <ul>
     * <li> Octal escapes are not recognized.
     *
     * <li> The character sequence <code>\b</code> does <i>not</i>
     * represent a backspace character.
     *
     * <li> The method does not treat a backslash character,
     * <code>\</code>, before a non-valid escape character as an
     * error; the backslash is silently dropped.  For example, in a
     * Java string the sequence <code>"\z"</code> would cause a
     * compile time error.  In contrast, this method silently drops
     * the backslash.  Therefore, this method treats the two character
     * sequence <code>"\b"</code> as equivalent to the single
     * character <code>'b'</code>.
     *
     * <li> Escapes are not necessary for single and double quotes;
     * however, by the rule above, single and double quote characters
     * preceded by a backslash still yield single and double quote
     * characters, respectively.
     *
     * <li> Only a single 'u' character is allowed in a Uniocde escape
     * sequence.
     *
     * </ul>
     * <p>
     * The specified stream remains open after this method returns.
     *
     * @param   reader   the input character stream.
     * @throws java.io.IOException  if an error occurred when reading from the
     *          input stream.
     * @throws  IllegalArgumentException if a malformed Unicode escape
     *          appears in the input.
     * @since   1.6
     */
    public synchronized void load(Reader reader) throws IOException {
        load0(new LineReader(reader));
    }

    /**
     * Reads a property list (key and element pairs) from the input
     * byte stream. The input stream is in a simple line-oriented
     * format as specified in
     * {@link #load(java.io.Reader) load(Reader)} and is assumed to use
     * the ISO 8859-1 character encoding; that is each byte is one Latin1
     * character. Characters not in Latin1, and certain special characters,
     * are represented in keys and elements using
     * <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.3">Unicode escapes</a>.
     * <p>
     * The specified stream remains open after this method returns.
     *
     * @param      inStream   the input stream.
     * @exception  IOException  if an error occurred when reading from the
     *             input stream.
     * @throws	   IllegalArgumentException if the input stream contains a
     * 		   malformed Unicode escape sequence.
     * @since 1.2
     */
    public synchronized void load(InputStream inStream) throws IOException {
        load(new InputStreamReader(inStream, "utf-8"));
    }

    private void load0 (LineReader lr) throws IOException {
        char[] convtBuf = new char[1024];
        int limit;
        int keyLen;
        int valueStart;
        char c;
        boolean hasSep;
        boolean precedingBackslash;

        while ((limit = lr.readLine()) >= 0) {
            c = 0;
            keyLen = 0;
            valueStart = limit;
            hasSep = false;

            //System.out.println("line=<" + new String(lineBuf, 0, limit) + ">");
            precedingBackslash = false;
            while (keyLen < limit) {
                c = lr.lineBuf[keyLen];
                //need check if escaped.
                if ((c == '=') && !precedingBackslash) {
                    valueStart = keyLen + 1;
                    hasSep = true;
                    break;
                } else if ((c == ' ' || c == '\t' ||  c == '\f') && !precedingBackslash) {
                    valueStart = keyLen + 1;
                    break;
                }
                if (c == '\\') {
                    precedingBackslash = !precedingBackslash;
                } else {
                    precedingBackslash = false;
                }
                keyLen++;
            }
            while (valueStart < limit) {
                c = lr.lineBuf[valueStart];
                if (c != ' ' && c != '\t' &&  c != '\f') {
                    if (!hasSep && (c == '=')) {
                        hasSep = true;
                    } else {
                        break;
                    }
                }
                valueStart++;
            }
            String key = loadConvert(lr.lineBuf, 0, keyLen, convtBuf);
            String value = loadConvert(lr.lineBuf, valueStart, limit - valueStart, convtBuf);
            put(key, value);
        }
    }

    /* Read in a "logical line" from an InputStream/Reader, skip all comment
     * and blank lines and filter out those leading whitespace characters 
     * (\u0020, \u0009 and \u000c) from the beginning of a "natural line". 
     * Method returns the char length of the "logical line" and stores 
     * the line in "lineBuf". 
     */
    class LineReader {
        public LineReader(InputStream inStream) {
            this.inStream = inStream;
            inByteBuf = new byte[8192];
        }

        public LineReader(Reader reader) {
            this.reader = reader;
            inCharBuf = new char[8192];
        }

        byte[] inByteBuf;
        char[] inCharBuf;
        char[] lineBuf = new char[1024];
        int inLimit = 0;
        int inOff = 0;
        InputStream inStream;
        Reader reader;

        int readLine() throws IOException {
            int len = 0;
            char c = 0;

            boolean isCommentLine = false;
            boolean isNewLine = true;
            boolean appendedLineBegin = false;
            boolean precedingBackslash = false;
            boolean skipLF = false;

            while (true) {
                if (inOff >= inLimit) {
                    inLimit = (inStream==null)?reader.read(inCharBuf)
                            :inStream.read(inByteBuf);
                    inOff = 0;
                    if (inLimit <= 0) {
                        if (len == 0 || isCommentLine) {
                            return -1;
                        }
                        return len;
                    }
                }
                if (inStream != null) {
                    //The line below is equivalent to calling a 
                    //ISO8859-1 decoder.
                    c = (char) (0xff & inByteBuf[inOff++]);
                } else {
                    c = inCharBuf[inOff++];
                }
                if (skipLF) {
                    skipLF = false;
                    if (c == '\n') {
                        continue;
                    }
                }
                if (isNewLine) {
                    isNewLine = false;
                    if (c == '#' || c == '!') {
                        isCommentLine = true;
                        continue;
                    }
                }

                if (c != '\n' && c != '\r') {
                    lineBuf[len++] = c;
                    if (len == lineBuf.length) {
                        int newLength = lineBuf.length * 2;
                        if (newLength < 0) {
                            newLength = Integer.MAX_VALUE;
                        }
                        char[] buf = new char[newLength];
                        System.arraycopy(lineBuf, 0, buf, 0, lineBuf.length);
                        lineBuf = buf;
                    }
                    //flip the preceding backslash flag
                    if (c == '\\') {
                        precedingBackslash = !precedingBackslash;
                    } else {
                        precedingBackslash = false;
                    }
                }
                else {
                    // reached EOL
                    if (isCommentLine || len == 0) {
                        isCommentLine = false;
                        isNewLine = true;
                        len = 0;
                        continue;
                    }
                    if (inOff >= inLimit) {
                        inLimit = (inStream==null)
                                ?reader.read(inCharBuf)
                                :inStream.read(inByteBuf);
                        inOff = 0;
                        if (inLimit <= 0) {
                            return len;
                        }
                    }
                    if (precedingBackslash) {
                        len -= 1;
                        appendedLineBegin = true;
                        precedingBackslash = false;
                        if (c == '\r') {
                            skipLF = true;
                        }
                    } else {
                        return len;
                    }
                }
            }
        }
    }

    /*
    * Converts encoded &#92;uxxxx to unicode chars
    * and changes special saved chars to their original forms
    */
    private String loadConvert (char[] in, int off, int len, char[] convtBuf) {
        if (convtBuf.length < len) {
            int newLen = len * 2;
            if (newLen < 0) {
                newLen = Integer.MAX_VALUE;
            }
            convtBuf = new char[newLen];
        }
        char aChar;
        char[] out = convtBuf;
        int outLen = 0;
        int end = off + len;

        while (off < end) {
            aChar = in[off++];
            if (aChar == '\\') {
                aChar = in[off++];
                if(aChar == 'u') {
                    // Read the xxxx
                    int value=0;
                    for (int i=0; i<4; i++) {
                        aChar = in[off++];
                        switch (aChar) {
                            case '0': case '1': case '2': case '3': case '4':
                            case '5': case '6': case '7': case '8': case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a': case 'b': case 'c':
                            case 'd': case 'e': case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A': case 'B': case 'C':
                            case 'D': case 'E': case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed \\uxxxx encoding.");
                        }
                    }
                    out[outLen++] = (char)value;
                } else {
                    if (aChar == 't') aChar = '\t';
                    else if (aChar == 'r') aChar = '\r';
                    else if (aChar == 'n') aChar = '\n';
                    else if (aChar == 'f') aChar = '\f';
                    out[outLen++] = aChar;
                }
            } else {
                out[outLen++] = (char)aChar;
            }
        }
        return new String (out, 0, outLen);
    }

    /*
     * Converts unicodes to encoded &#92;uxxxx and escapes
     * special characters with a preceding slash
     */
    private String saveConvert(String theString,
                               boolean escapeSpace,
                               boolean escapeUnicode) {
        int len = theString.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        StringBuffer outBuffer = new StringBuffer(bufLen);

        for(int x=0; x<len; x++) {
            char aChar = theString.charAt(x);
            // Handle common case first, selecting largest block that
            // avoids the specials below
            if ((aChar > 61) && (aChar < 127)) {
                if (aChar == '\\') {
                    outBuffer.append('\\'); outBuffer.append('\\');
                    continue;
                }
                outBuffer.append(aChar);
                continue;
            }
            switch(aChar) {
                case ' ':
                    if (x == 0 || escapeSpace)
                        outBuffer.append('\\');
                    outBuffer.append(' ');
                    break;
                case '\n': outBuffer.append('\\');outBuffer.append('n'); outBuffer.append('\\');outBuffer.append('\n');
                    break;
                case '\r':outBuffer.append('\\'); outBuffer.append('r');
                    break;
                case '\f':outBuffer.append('\\'); outBuffer.append('f');
                    break;
                case '#': // Fall through
                case '!':
                    outBuffer.append('\\'); outBuffer.append(aChar);
                    break;
                default:
                    if (((aChar < 0x0020) || (aChar > 0x007e)) & escapeUnicode ) {
                        outBuffer.append('\\');
                        outBuffer.append('u');
                        outBuffer.append(toHex((aChar >> 12) & 0xF));
                        outBuffer.append(toHex((aChar >>  8) & 0xF));
                        outBuffer.append(toHex((aChar >>  4) & 0xF));
                        outBuffer.append(toHex( aChar        & 0xF));
                    } else {
                        outBuffer.append(aChar);
                    }
            }
        }
        return outBuffer.toString();
    }

    /**
     * Writes this property list (key and element pairs) in this
     * <code>Properties</code> table to the output character stream in a 
     * format suitable for using the {@link #load(java.io.Reader) load(Reader)}
     * method.
     * <p>
     * Properties from the defaults table of this <code>Properties</code>
     * table (if any) are <i>not</i> written out by this method.
     * <p>
     * If the comments argument is not null, then an ASCII <code>#</code>
     * character, the comments string, and a line separator are first written
     * to the output stream. Thus, the <code>comments</code> can serve as an
     * identifying comment. Any one of a line feed ('\n'), a carriage
     * return ('\r'), or a carriage return followed immediately by a line feed
     * in comments is replaced by a line separator generated by the <code>Writer</code>
     * and if the next character in comments is not character <code>#</code> or 
     * character <code>!</code> then an ASCII <code>#</code> is written out 
     * after that line separator.
     * <p>
     * Next, a comment line is always written, consisting of an ASCII
     * <code>#</code> character, the current date and time (as if produced
     * by the <code>toString</code> method of <code>Date</code> for the
     * current time), and a line separator as generated by the <code>Writer</code>.
     * <p>
     * Then every entry in this <code>Properties</code> table is
     * written out, one per line. For each entry the key string is
     * written, then an ASCII <code>=</code>, then the associated
     * element string. For the key, all space characters are
     * written with a preceding <code>\</code> character.  For the
     * element, leading space characters, but not embedded or trailing
     * space characters, are written with a preceding <code>\</code>
     * character. The key and element characters <code>#</code>,
     * <code>!</code>, <code>=</code>, and <code>:</code> are written
     * with a preceding backslash to ensure that they are properly loaded.
     * <p>
     * After the entries have been written, the output stream is flushed.  
     * The output stream remains open after this method returns.
     * <p>
     *
     * @param   writer      an output character stream writer.
     * @exception  IOException if writing this property list to the specified
     *             output stream throws an <tt>IOException</tt>.
     * @exception  ClassCastException  if this <code>Properties</code> object
     *             contains any keys or values that are not <code>Strings</code>.
     * @exception  NullPointerException  if <code>writer</code> is null.
     * @since 1.6
     */
    public void store(Writer writer)
            throws IOException
    {
        store0((writer instanceof BufferedWriter) ? (BufferedWriter) writer
                : new BufferedWriter(writer), false);
    }

    /**
     * Writes this property list (key and element pairs) in this
     * <code>Properties</code> table to the output stream in a format suitable
     * for loading into a <code>Properties</code> table using the
     * {@link #load(InputStream) load(InputStream)} method.
     * <p>
     * Properties from the defaults table of this <code>Properties</code>
     * table (if any) are <i>not</i> written out by this method.
     * <p>
     * This method outputs the comments, properties keys and values in 
     * the same format as specified in
     * {@link #store(java.io.Writer) store(Writer)},
     * with the following differences:
     * <ul>
     * <li>The stream is written using the ISO 8859-1 character encoding.
     *
     * <li>Characters not in Latin-1 in the comments are written as 
     * <code>&#92;u</code><i>xxxx</i> for their appropriate unicode 
     * hexadecimal value <i>xxxx</i>. 
     *
     * <li>Characters less than <code>&#92;u0020</code> and characters greater
     * than <code>&#92;u007E</code> in property keys or values are written
     * as <code>&#92;u</code><i>xxxx</i> for the appropriate hexadecimal
     * value <i>xxxx</i>. 
     * </ul>
     * <p>
     * After the entries have been written, the output stream is flushed.  
     * The output stream remains open after this method returns.
     * <p>
     * @param   out      an output stream.
     * @exception  IOException if writing this property list to the specified
     *             output stream throws an <tt>IOException</tt>.
     * @exception  ClassCastException  if this <code>Properties</code> object
     *             contains any keys or values that are not <code>Strings</code>.
     * @exception  NullPointerException  if <code>out</code> is null.
     * @since 1.2
     */
    public void store(OutputStream out)
            throws IOException
    {
        store0(new BufferedWriter(new OutputStreamWriter(out, "utf-8")), false);
    }

    private void store0(BufferedWriter bw,boolean escUnicode)
            throws IOException
    {
        synchronized (this) {
            for (String key : keySet()) {
                String val = (String)get(key);
                key = saveConvert(key, true, escUnicode);
                /* No need to escape embedded and trailing spaces for value, hence
             * pass false to flag.
             */
                val = saveConvert(val, false, escUnicode);
                bw.write(key + "=" + val);
                bw.newLine();
            }
        }
        bw.flush();
    }

    /**
     * Searches for the property with the specified key in this property list.
     * If the key is not found in this property list, the default property list,
     * and its defaults, recursively, are then checked. The method returns
     * <code>null</code> if the property is not found.
     *
     * @param   key   the property key.
     * @return  the value in this property list with the specified key value.
     * @see     #setProperty
     */
    public String getProperty(String key) {
        Object oval = super.get(key);
        String sval = (oval instanceof String) ? (String)oval : null;
        return sval;
    }

    /**
     * Searches for the property with the specified key in this property list.
     * If the key is not found in this property list, the default property list,
     * and its defaults, recursively, are then checked. The method returns the
     * default value argument if the property is not found.
     *
     * @param   key            the hashtable key.
     * @param   defaultValue   a default value.
     *
     * @return  the value in this property list with the specified key value.
     * @see     #setProperty
     */
    public String getProperty(String key, String defaultValue) {
        String val = getProperty(key);
        return (val == null) ? defaultValue : val;
    }

    /**
     * Returns an enumeration of all the keys in this property list,
     * including distinct keys in the default property list if a key
     * of the same name has not already been found from the main
     * properties list.
     *
     * @return  an enumeration of all the keys in this property list, including
     *          the keys in the default property list.
     * @throws  ClassCastException if any key in this property list
     *          is not a string. 
     * @see     java.util.Enumeration
     * @see     java.util.Properties#defaults
     * @see     #stringPropertyNames
     */
    public Enumeration<?> propertyNames() {
        Hashtable h = new Hashtable();
        enumerate(h);
        return h.keys();
    }

    /**
     * Returns a set of keys in this property list where
     * the key and its corresponding value are strings,
     * including distinct keys in the default property list if a key
     * of the same name has not already been found from the main
     * properties list.  Properties whose key or value is not 
     * of type <tt>String</tt> are omitted.
     * <p>
     * The returned set is not backed by the <tt>Properties</tt> object.
     * Changes to this <tt>Properties</tt> are not reflected in the set,
     * or vice versa.
     *
     * @return  a set of keys in this property list where
     *          the key and its corresponding value are strings,
     *          including the keys in the default property list.
     * @see     java.util.Properties#defaults
     * @since   1.6
     */
    public Set<String> stringPropertyNames() {
        Hashtable<String, String> h = new Hashtable<String, String>();
        enumerateStringProperties(h);
        return h.keySet();
    }

    /**
     * Prints this property list out to the specified output stream.
     * This method is useful for debugging.
     *
     * @param   out   an output stream.
     * @throws  ClassCastException if any key in this property list
     *          is not a string. 
     */
    public void list(PrintStream out) {
        out.println("-- listing properties --");
        Hashtable h = new Hashtable();
        enumerate(h);
        for (Enumeration e = h.keys() ; e.hasMoreElements() ;) {
            String key = (String)e.nextElement();
            String val = (String)h.get(key);
            if (val.length() > 40) {
                val = val.substring(0, 37) + "...";
            }
            out.println(key + "=" + val);
        }
    }

    /**
     * Prints this property list out to the specified output stream.
     * This method is useful for debugging.
     *
     * @param   out   an output stream.
     * @throws  ClassCastException if any key in this property list
     *          is not a string. 
     * @since   JDK1.1
     */
    /*
     * Rather than use an anonymous inner class to share common code, this
     * method is duplicated in order to ensure that a non-1.1 compiler can
     * compile this file.
     */
    public void list(PrintWriter out) {
        out.println("-- listing properties --");
        Hashtable h = new Hashtable();
        enumerate(h);
        for (Enumeration e = h.keys() ; e.hasMoreElements() ;) {
            String key = (String)e.nextElement();
            String val = (String)h.get(key);
            if (val.length() > 40) {
                val = val.substring(0, 37) + "...";
            }
            out.println(key + "=" + val);
        }
    }

    /**
     * Enumerates all key/value pairs in the specified hashtable.
     * @param h the hashtable
     * @throws ClassCastException if any of the property keys
     *         is not of String type.
     */
    private synchronized void enumerate(Hashtable h) {
        for (String key:keySet()) {
            h.put(key, get(key));
        }
    }

    /**
     * Enumerates all key/value pairs in the specified hashtable
     * and omits the property if the key or value is not a string.
     * @param h the hashtable
     */
    private synchronized void enumerateStringProperties(Hashtable<String, String> h) {
        for (String k:keySet()) {
            Object v = get(k);
            if (k instanceof String && v instanceof String) {
                h.put((String) k, (String) v);
            }
        }
    }

    /**
     * Convert a nibble to a hex character
     * @param	nibble	the nibble to convert.
     */
    private static char toHex(int nibble) {
        return hexDigit[(nibble & 0xF)];
    }

    /** A table of hex digits */
    private static final char[] hexDigit = {
            '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
    };
}
