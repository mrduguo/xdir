package org.duguo.xdir.http.json.impl.parser;

import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.http.json.JsonMap;
import org.duguo.xdir.http.json.impl.JsonMapImpl;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;


/**
 * Parse query string as json object
 * 
 */
public class QueryStringConverter extends AbstractTextConverter {

 
    /**
     *
     * @param reader The reader
     * @throws IOException If an error occurs.
     */
    protected Json doParse(Reader reader) throws IOException {
    	JsonMap rootJson=new JsonMapImpl();
        
    	parseElements(reader, rootJson);
        
        return rootJson;
    }


    protected void parseElements(Reader reader, JsonMap rootJson)  throws IOException{
        try{
        	int next=reader.read();
        	StringBuilder buffer=null;
        	while (next != XmlConverterUtil.EOF) { 
    			buffer=new StringBuilder();
    			buffer.append((char)next);
    			XmlConverterUtil.readToNextChar(reader,buffer,'=');
    			String key=buffer.toString().trim();

    			buffer=new StringBuilder();
    			XmlConverterUtil.readToNextChar(reader,buffer,'&');
    			rootJson.set(key, URLDecoder.decode(buffer.toString().trim(), "UTF-8"));
    			
    			next=reader.read();
	        }

        }catch (IOException ex){
        	throw ex;
        }
	}


	@Override
	protected void doSerialize(Json json, Writer writer) throws IOException {
		boolean isFirst=true;
		for(Map.Entry<String, Json> entry:json.map().get().entrySet()){
			if(isFirst){
				isFirst=false;
			}else{
				writer.write('&');
			}
			writer.write(entry.getKey());
			writer.write('=');
			writer.write(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
		}
	}

}
