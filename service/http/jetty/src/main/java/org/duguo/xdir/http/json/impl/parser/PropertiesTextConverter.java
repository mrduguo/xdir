package org.duguo.xdir.http.json.impl.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.http.json.JsonMap;
import org.duguo.xdir.http.json.impl.JsonMapImpl;


/**
 * Parse properties files
 * 
 */
public class PropertiesTextConverter extends AbstractTextConverter {

 
    private char propertySpliter='=';
	
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
        		if(next=='#'){
        			// for comment line
        			buffer=new StringBuilder();
        			XmlConverterUtil.readToNextChar(reader,buffer,'\n');
        		}else{
        			buffer=new StringBuilder();
        			buffer.append((char)next);
        			XmlConverterUtil.readToNextChar(reader,buffer,propertySpliter);
        			String key=buffer.toString().trim();

        			buffer=new StringBuilder();
        			XmlConverterUtil.readToNextChar(reader,buffer,'\n');
        			rootJson.set(key, buffer.toString().trim());		
        		}
    			next=reader.read();
	        }

        }catch (IOException ex){
        	throw ex;
        }
	}


	@Override
	protected void doSerialize(Json json, Writer writer) throws IOException {
		for(Map.Entry<String, Json> entry:json.map().get().entrySet()){
			writer.write(entry.getKey());
			writer.write(propertySpliter);
			writer.write(entry.getValue().toString());
			writer.write('\n');
		}
	}



	public char getPropertySpliter() {
		return propertySpliter;
	}


	public void setPropertySpliter(char propertySpliter) {
		this.propertySpliter = propertySpliter;
	}
}
