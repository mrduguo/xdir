package org.duguo.xdir.infra.http.rest.impl.provider;


import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces("text/plain")
public class TextMessageBodyWriter implements MessageBodyWriter<Object> {
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Object.class.isAssignableFrom(type);
    }
    
    public long getSize(Object obj, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public void writeTo(Object obj, Class type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        String resultString=null;
        Class primitiveClass=ClassUtils.wrapperToPrimitive(obj.getClass());
        if(primitiveClass!=null){
            resultString=String.valueOf(obj);
        }else if(obj instanceof String){
            resultString=(String)obj;
        }else{
            resultString=ToStringBuilder.reflectionToString(obj, ToStringStyle.MULTI_LINE_STYLE);
        }
        entityStream.write(resultString.getBytes());
    }
}