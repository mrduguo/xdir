package org.duguo.xdir.http.json.impl.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duguo.xdir.http.json.Json;
import org.duguo.xdir.http.json.JsonMap;
import org.duguo.xdir.http.json.impl.JsonMapImpl;


/**
 * Part of the parse algorithm is come from @see org.json.XML
 * 
 */
public class XmlJsonTextConverter extends AbstractTextConverter {

    private static final Logger logger = LoggerFactory.getLogger( XmlJsonTextConverter.class );
 
	
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


        Stack<Json> objectStack = new Stack<Json>();
        objectStack.push(rootJson);
        int next = XmlConverterUtil.readToNextTagStart(reader);
        boolean attributeStart=false;
        try{
	        while (next != XmlConverterUtil.EOF) {
	            switch (next) {
	            	case '?':
	            		// e.g. <?xml version=\"1.0\"?>
                    	StringBuilder metaData=new StringBuilder();
                    	metaData.append("<?");
                    	next = XmlConverterUtil.readToCurrentTagEnd(reader,metaData);
                    	objectStack.peek().map().add("_meta", metaData);
                    	next = XmlConverterUtil.readToNextTagStart(reader);
	            		break;
	            	case '!':
                    	next=reader.read();
                        if (next == '[') {
                        	 // cdata e.g. <![CDATA[description text here]]>
                        	String text=XmlConverterUtil.readCdata(reader);
	                    	objectStack.peek().map().set("text", text);
                        }else  if (next == '-') {
                        	// e.g. <!--comments-->
                        	metaData=new StringBuilder();
                        	metaData.append("<!-");
                        	XmlConverterUtil.readComment(reader,metaData);
                        	objectStack.peek().map().add("_meta", metaData.toString());
                        }else{
                        	// e.g. <!DOCTYPE log4j:configuration SYSTEM \"log4j.dtd\">
                        	metaData=new StringBuilder();
                        	metaData.append("<!");
                        	next = XmlConverterUtil.readToCurrentTagEnd(reader,metaData);
                        	objectStack.peek().map().add("_meta", metaData.toString());
	                    }
                        next = XmlConverterUtil.readToNextTagStart(reader);
	            		break;
	            	case '/':
                		next = XmlConverterUtil.readIgnoreWhitespace(reader);
                    	if(next=='>'){
                        	// e.g. <opentag foo="bar" />
                        	// e.g. <opentag/>
                    	}else{
                        	// e.g. </closetag>
                        	metaData=new StringBuilder();
                    		XmlConverterUtil.readToCurrentTagEnd(reader,metaData);
                    	}
                		next = XmlConverterUtil.readIgnoreWhitespace(reader);
                    	attributeStart=false;
                		objectStack.pop();
                    	break;
	            	case '<':
                    	// e.g. <opentag foo="bar" />
                    	next=reader.read();
                    	break;
	            	case '>':
                        next = XmlConverterUtil.readIgnoreWhitespace(reader);
                        if(next!='<'){
                        	// e.g. <tag>text</tag>
	                    	StringBuilder text=new StringBuilder();
	                    	next = XmlConverterUtil.readText(reader,text,next);
	                    	objectStack.peek().map().set("text", text);
                        }else{
                        	// e.g. <tag><child/></tag>
                        	next = XmlConverterUtil.readIgnoreWhitespace(reader);
                        }
                    	attributeStart=false;
                        break;
	                default:
	                	if(attributeStart){
                        	// e.g. <tag foo="bar" ...
	                		StringBuilder attrName=new StringBuilder();
	                		attrName.append('@');
	                		attrName.append((char)next);
	                    	next = XmlConverterUtil.readKey(reader,attrName);
	                    	
	                    	next = XmlConverterUtil.readIgnoreWhitespace(reader);
	                		String attrValue=XmlConverterUtil.readAttributeValue(reader);
	                    	objectStack.peek().map().set(attrName.toString(), attrValue);

                        	next = XmlConverterUtil.readIgnoreWhitespace(reader);
	                	}else{
                        	// e.g. <tag
	                    	StringBuilder key=new StringBuilder();
	                    	key.append((char)next);
	                    	next = XmlConverterUtil.readKey(reader,key);
	                    	JsonMap newElement=new JsonMapImpl();
	                    	objectStack.peek().map().add(key.toString(), newElement);
	                    	objectStack.push(newElement);

	                    	if(next!='>' && next!='/'){
	                        	// e.g. <tag foo="bar" ...
	                    		next = XmlConverterUtil.readIgnoreWhitespace(reader);
	            	            attributeStart=true;
	                    	}else{
	                        	// e.g. <tag/>
	                        	// e.g. <tag>
	                    	}
	                	}
                        	
	            }
	        }

        }catch (IOException ex){
        	if(next == XmlConverterUtil.EOF){
            	logger.error(ex.getMessage()+": Unexpected object termination\nparsed json object:\n{}",rootJson);
        	}else{
            	String remainText=JsonConverterUtil.readToEnd(reader);
            	if(remainText.length()>0){
                	logger.error(ex.getMessage()+"\nparsed json object:\n{}\nremain text:\n{}",rootJson,((char)next)+remainText);
            	}else{
                	logger.error(ex.getMessage()+"\nparsed json object:\n{}",rootJson);            		
            	}
        	}
        	throw ex;
        }
	}


	@Override
	protected void doSerialize(Json json, Writer writer) throws IOException {
		toXmlText(json, writer,null,-1);
	}

	protected void toXmlText(Json json,Writer writer,String tag,int indentLevel) throws IOException  {
		if(json.isMap()){
			boolean hasChild=false;
			if(tag!=null){
				writeIndent(writer,indentLevel);
				writer.append("<");
				writer.append(tag);
			}
			for(Map.Entry<String, Json> entry:json.map().get().entrySet()){
				String key=entry.getKey();
				if(key.charAt(0)=='@'){
					writer.append(" ");
					writer.append(key.substring(1));
					writer.append("=\"");
					writer.append(XmlConverterUtil.escape(entry.getValue().value().toString()));
					writer.append("\"");
				}else{
					hasChild=true;
				}
			}
			if(tag!=null){
				if(hasChild){
					writer.append(">");
				}else{
					writer.append("/>");
				}
			}
			if(hasChild){
				boolean isMultipleLine=false;
				for(Map.Entry<String, Json> entry:json.map().get().entrySet()){
					String key=entry.getKey();
					if(key.charAt(0)=='_'){
						if(entry.getValue().isArray()){
							for(Json value:entry.getValue().array().get()){
								writeIndent(writer,indentLevel+1);
								writer.append(value.toString());
							}
						}else{
							writeIndent(writer,indentLevel+1);
							writer.append(entry.getValue().value().toString());
						}
					}else if(key.charAt(0)!='@'){
						if(key.equals("text")){
							String textValue=entry.getValue().value().toString();
							if(textValue.indexOf("\n")>0){
								writeIndent(writer,indentLevel+1);
								writer.append(XmlConverterUtil.escape(textValue));
								isMultipleLine=true;
							}else{
								writer.append(XmlConverterUtil.escape(textValue));										
							}						
						}else{
							toXmlText(entry.getValue(), writer,key,indentLevel+1);
							isMultipleLine=true;
						}
					}
				}
				if(tag!=null){
					if(isMultipleLine){
						writeIndent(writer,indentLevel);
					}					
					writer.append("</");
					writer.append(tag);
					writer.append(">");					
				}
			}
		}else if(json.isArray()){
			for(Json child:json.array().get()){
				toXmlText(child, writer,tag,indentLevel);				
			}
		}else{
			throw new RuntimeException("cannot convert to xml format at element: "+json.toString());			
		}
	}
}
