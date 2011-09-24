package org.duguo.xdir.http.json;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;


/**
 *  Convert between json object and other text based sources 
 * 
 */
public interface JsonTextConverter extends JsonParser,JsonSerializer{
	
    public Json parse(String str) throws IOException;

    /**
     *
     * @param file File to be parsed
     * 
     * @throws IOException If an error occurs.
     */
    public Json parse(File file) throws IOException;

    /**
     *
     * @param url URL to be parsed
     * 
     * @throws IOException If an error occurs.
     */
    public Json parse(URL url) throws IOException;
    
    /**
     *
     * @param input InputStream to be parsed.
     * @throws IOException If an error occurs.
     */
    public Json parse(InputStream input) throws IOException;
    /**
     *
     * @param input InputStream to be parsed.
     * @param charSetName Name of the charset to be used.
     * @throws IOException If an error occurs.
     */
    public Json parse(InputStream input, String charSetName) throws IOException;
    /**
     *
     * @param reader The reader
     * @throws IOException If an error occurs.
     */
    public Json parse(Reader reader) throws IOException;
    
	/**
	 * convert the json object to text format
	 * 
	 * @param json
	 * @return
	 */
    public String serialize(Json json);
    
}
