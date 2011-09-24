package org.duguo.xdir.http.json.impl.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.http.json.JsonArray;
import org.duguo.xdir.http.json.JsonMap;
import org.duguo.xdir.http.json.impl.JsonArrayImpl;
import org.duguo.xdir.http.json.impl.JsonMapImpl;
import org.duguo.xdir.http.json.impl.JsonNull;
import org.duguo.xdir.http.json.impl.JsonValueImpl;


/**
 * Default json string parser. It support single ' and double " quota. Root object could be array.
 * 
 * Sample valid input:
 * {"key":"value"}
 * [1,2,3,4]
 * {"stringkey":"stringvalue", "intkey":2147483647, "longkey":9223372036854775807, "doublekey":1.7976931348623157E308, "booleankey":true, "arraykey":["stringvalue", 2147483647], "nullkey":null}
 * 
 * Part of the parse algorithm is come from @see org.apache.jackrabbit.commons.json.JsonParser
 * 
 * @author mrduguo
 */
public class DefaultJsonTextConverter extends AbstractTextConverter {
    
    public static final DefaultJsonTextConverter DEFAULT_JSON_TEXT_CONVERT=new DefaultJsonTextConverter(); 
	
	private static final String NULL = "null";

    /**
     *
     * @param reader The reader
     * @throws IOException If an error occurs.
     */
    protected Json doParse(Reader reader) throws IOException {
        Json rootJson=null;
        //create root json object
        int next = JsonConverterUtil.readIgnoreWhitespace(reader);
        if (next == '{') {
        	rootJson=parseMap(reader);
        } else if (next == '[') {
        	rootJson=parseArray(reader);
        } else {
        	throw new RuntimeException("JSON object must start with a '{' or '[', while it start with '"+((char)next)+"'");
        }        
        return rootJson;
    }
    
    protected JsonMap parseMap(Reader reader)throws IOException{
    	JsonMap json=new JsonMapImpl();
        for (;;) {
        	int next = JsonConverterUtil.readIgnoreWhitespace(reader);
        	// key
        	String key = JsonConverterUtil.nextString(reader,(char)next);
        	
        	// next is ':'
            next = JsonConverterUtil.readIgnoreWhitespace(reader);
            
            // value
            next = JsonConverterUtil.readIgnoreWhitespace(reader);
            Json value=null;
            if (next == '{') {
            	value=parseMap(reader);
                next = JsonConverterUtil.readIgnoreWhitespace(reader);
            } else if (next == '[') {
            	value=parseArray(reader);
                next = JsonConverterUtil.readIgnoreWhitespace(reader);
            } else if (next == '"' || next=='\'') {
            	String valueString=JsonConverterUtil.nextString(reader, (char)next);
            	value=new JsonValueImpl(valueString);
                next = JsonConverterUtil.readIgnoreWhitespace(reader);
            } else {
                // simple value
            	StringBuilder valueBuilder = new StringBuilder();
            	next = JsonConverterUtil.readSimpleValue(reader, valueBuilder, next);
            	value=parseSimpleValue(valueBuilder);
            }
            json.set(key, value);
            if(next!=','){
            	break;
            }
        }
    	return json;
    }
    
    protected JsonArray parseArray(Reader reader)throws IOException{
    	JsonArray json=new JsonArrayImpl();
        for (;;) {
        	int next = JsonConverterUtil.readIgnoreWhitespace(reader);
        	
            // value
            Json value=null;
            if (next == '{') {
            	value=parseMap(reader);
                next = JsonConverterUtil.readIgnoreWhitespace(reader);
            } else if (next == '[') {
            	value=parseArray(reader);
                next = JsonConverterUtil.readIgnoreWhitespace(reader);
            } else if (next == '"' || next=='\'') {
            	String valueString=JsonConverterUtil.nextString(reader, (char)next);
            	value=new JsonValueImpl(valueString);
                next = JsonConverterUtil.readIgnoreWhitespace(reader);
            } else {
                // simple value
            	StringBuilder valueBuilder = new StringBuilder();
            	next = JsonConverterUtil.readSimpleValue(reader, valueBuilder, next);
            	value=parseSimpleValue(valueBuilder);
            }
            json.add(value);
            if(next!=','){
            	break;
            }
        }    	
    	return json;
    }

	protected Json parseSimpleValue(StringBuilder valueBuilder) throws IOException {
        String valueString = valueBuilder.toString().trim();
        Json value=null;
        if (NULL.equals(valueString)) {
        	value=JsonNull.instance();
        } else if (valueString.equalsIgnoreCase("true")) {
        	value=new JsonValueImpl(true);
        } else if (valueString.equalsIgnoreCase("false")) {
        	value=new JsonValueImpl(false);
        } else if (valueString.indexOf('.') > -1) {
            double d = Double.parseDouble(valueString);
            value= new JsonValueImpl(d);
        } else {
            long l = Long.parseLong(valueString);
            value= new JsonValueImpl(l);
        }
        return value;
    }

	@Override
	protected void doSerialize(Json json, Writer writer) throws IOException {
		toJsonStyleText(json, writer,0);
	}

	private void toJsonStyleText(Json json,Writer writer,int indentLevel) throws IOException  {
		if(json.isValue()){
			if(json.isNull()){
				writer.append("null");
			}else{
				Object value=json.value().get();
				if(value instanceof Integer || value instanceof Long || value instanceof Double || value instanceof Boolean){
					writer.append(value.toString());
				}else{
					writer.append('"');
					writer.append(JsonConverterUtil.escape(value.toString()));
					writer.append('"');
				}
			}
		}else if(json.isMap()){
			writeIndent(writer,indentLevel);
			writer.append("{");
			indentLevel++;
			if(!json.isNull()){
				boolean isFirst=true;
				for(Map.Entry<String, Json> entry:json.map().get().entrySet()){
					if(isFirst){
						isFirst=false;
					}else{
						writer.append(",");
					}
					writeIndent(writer,indentLevel);
					writer.append('"');
					writer.append(JsonConverterUtil.escape(entry.getKey()));
					writer.append('"');
					writer.append(":");
					toJsonStyleText(entry.getValue(),writer,indentLevel+1);
				}
			}
			indentLevel--;
			writeIndent(writer,indentLevel);
			writer.append("}");
		}else if(json.isArray()){
			writeIndent(writer,indentLevel);
			writer.append("[");
			indentLevel++;
			if(!json.isNull()){
				boolean isFirst=true;
				for(Json entry:json.array().get()){
					if(isFirst){
						isFirst=false;
					}else{
						writer.append(",");
					}
					if(entry.isValue()){
						writeIndent(writer,indentLevel);
						toJsonStyleText(entry,writer,indentLevel+1);
					}else{
						toJsonStyleText(entry,writer,entry.isValue()?indentLevel+1:indentLevel);
					}
				}
			}
			indentLevel--;
			writeIndent(writer,indentLevel);
			writer.append("]");
		}else{
			// json.isNull()
			writer.append("null");			
		}
	}

}
