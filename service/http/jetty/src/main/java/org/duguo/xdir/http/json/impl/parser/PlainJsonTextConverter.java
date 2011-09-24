package org.duguo.xdir.http.json.impl.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.http.util.CharArrayBuffer;
import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.http.json.JsonMap;
import org.duguo.xdir.http.json.impl.JsonMapImpl;


/**
 * Store the input as "text" in JsonMap
 * 
 */
public class PlainJsonTextConverter extends AbstractTextConverter {


    protected Json doParse(Reader reader) throws IOException {
    	JsonMap rootJson=new JsonMapImpl();

        CharArrayBuffer buffer = new CharArrayBuffer(1024); 
        char[] tmp = new char[1024];
        int l;
        while((l = reader.read(tmp)) != -1) {
            buffer.append(tmp, 0, l);
        }
        if(buffer.length()>0){
        	rootJson.set("text", buffer.toString());
        }
        
        return rootJson;
    }


	@Override
	protected void doSerialize(Json json, Writer writer) throws IOException {
		throw new UnsupportedOperationException(getClass().getSimpleName()+" doesn't support serialization");
	}

}
