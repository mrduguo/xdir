package org.duguo.xdir.http.json.impl.parser;

import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.http.json.JsonTextConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;


/**
 * Abstract class to support general json parse operation
 * 
 * @author mrduguo
 */
public abstract class AbstractTextConverter implements JsonTextConverter {

    private static final Logger logger = LoggerFactory.getLogger( AbstractTextConverter.class );

    private int indentSpace=0;

    /**
     * Leave to subclass to read from input reader and create json object.
     * 
     * @param reader Reader input 
     * @return
     * @throws IOException
     */
    protected abstract Json doParse(Reader reader) throws IOException;


    /**
     * Leave to subclass to generate the output from the json object.
     * 
     * @param writer Writer ouput 
     * @return
     * @throws IOException
     */
    protected abstract void doSerialize(Json json,Writer writer) throws IOException;

    /**
     *
     * @param file File to be parsed
     * 
     * @throws IOException If an error occurs.
     */
    public Json parse(Object source){
    	try {
    		if(source instanceof String){
    			return parse((String)source);
    		}else if(source instanceof File){
    			return parse((File)source);
    		}else if(source instanceof URL){
    			return parse((URL)source);
    		}else if(source instanceof InputStream){
    			return parse((InputStream)source);
    		}else if(source instanceof Reader){
    			return parse((Reader)source);
    		}else {
    			return parse(source.toString());
    		}
		} catch (IOException ex) {
			throw new RuntimeException("parse as json failed for object ["+source+"]",ex);
		}
    }
	
	/**
	 * convert the json object to another format
	 * 
	 * @param json
	 * @return
	 */
    public String serialize(Json json){
    	StringWriter writer = new StringWriter();
    	try {
			serialize(json, writer);
		} catch (IOException ex) {
			throw new RuntimeException("serialize failed for json ["+json+"]",ex);
		}
    	return writer.toString();
    }
	
	/**
	 * convert the json object to another format
	 * 
	 * @param json
	 * @param file
	 * @return
	 */
    public void serialize(Json json,File file) throws IOException{
    	OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
    	serialize(json, writer);
    }
	
	/**
	 * convert the json object to another format
	 * 
	 * @param json
	 * @param outputStream
	 * @return
	 */
    public void serialize(Json json,OutputStream outputStream) throws IOException{
    	OutputStreamWriter writer = new OutputStreamWriter(outputStream);
    	serialize(json, writer);
    }
	
	/**
	 * write the json object to writer with expected format
	 * 
	 * @param json
	 * @param writer
	 */
    public void serialize(Json json,Writer writer) throws IOException{
    	if(logger.isDebugEnabled()){
        	StringWriter stringWriter = new StringWriter();
    		doSerialize(json,stringWriter);
    		logger.debug("serialized json to string:\n{}",stringWriter.toString());
    		writer.write(stringWriter.toString());
    	}else{
    		doSerialize(json,writer);
    	}
    }

    /**
     *
     * @param inputString String to be parsed
     * 
     * @throws IOException If an error occurs.
     */
    public Json parse(String inputString) throws IOException {
       return parse(new BufferedReader(new StringReader(inputString)));
    }

    /**
     *
     * @param file File to be parsed
     * 
     * @throws IOException If an error occurs.
     */
    public Json parse(File file) throws IOException {
       return parse(new FileInputStream(file));
    }

    /**
     *
     * @param url URL to be parsed
     * 
     * @throws IOException If an error occurs.
     */
    public Json parse(URL url) throws IOException {
       return parse(url.openStream());
    }

    /**
     *
     * @param input InputStream to be parsed.
     * @throws IOException If an error occurs.
     */
    public Json parse(InputStream input) throws IOException {
        return parse(new BufferedReader(new InputStreamReader(input)));
    }

    /**
     *
     * @param input InputStream to be parsed.
     * @param charSetName Name of the charset to be used.
     * @throws IOException If an error occurs.
     */
    public Json parse(InputStream input, String charSetName) throws IOException {
        return parse(new BufferedReader(new InputStreamReader(input, charSetName)));
    }

    /**
     *
     * @param reader The reader
     * @throws IOException If an error occurs.
     */
    public Json parse(Reader reader) throws IOException {
    	Json json=null;
    	try{
	    	if(logger.isDebugEnabled()){
	    		// if debug enabled, display the input string first in case of parse failure.
	    		String inputString=JsonConverterUtil.readToEnd(reader);
	    		logger.debug("parser input string:\n{}",inputString);
	    		reader=new BufferedReader(new StringReader(inputString));
	    		json=doParse(reader);
	    		logger.debug("parsed json object:\n{}",json);
	    	}else{
	    		json=doParse(reader);
	    	}
    	}finally{
    		reader.close();
    	}
    	return json;
    }

	protected void writeIndent(Writer writer,int indentLevel)  throws IOException {
		if(indentSpace>0){
			writer.append("\n");
			for(int i=0;i<indentSpace*indentLevel;i++){
				writer.append(" ");
			}
		}
	}

	public int getIndentSpace() {
		return indentSpace;
	}

	public void setIndentSpace(int indentSpace) {
		this.indentSpace = indentSpace;
	}
}
