package org.duguo.xdir.infra.http.rest.impl.provider;


import org.apache.commons.lang3.StringUtils;
import org.duguo.xdir.infra.http.rest.api.model.ComponentInfo;
import org.duguo.xdir.infra.http.rest.api.model.ComponentsInfo;
import org.duguo.xdir.infra.http.rest.api.model.PingOk;

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
public class ComponentsTextMessageBodyWriter implements MessageBodyWriter<ComponentsInfo> {
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return ComponentsInfo.class.isAssignableFrom(type);
    }
    
    public long getSize(ComponentsInfo obj, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public void writeTo(ComponentsInfo obj, Class type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        StringBuilder textResponse=new StringBuilder();
        int maxComponentNameLength=0;
        for (ComponentInfo componentInfo : obj.getComponents()) {
            if(maxComponentNameLength<componentInfo.getName().length()){
               maxComponentNameLength=componentInfo.getName().length();
            }
        }
        appendSingleLine(textResponse, maxComponentNameLength, "NAME","STATUS","RESPONSE TIME [ms]");
        for (ComponentInfo componentInfo : obj.getComponents()) {
            appendSingleLine(textResponse, maxComponentNameLength, componentInfo.getName(),componentInfo.getStatus(),String.valueOf(componentInfo.getResponseTime()));
        }
        entityStream.write(textResponse.toString().getBytes());
    }

    private void appendSingleLine(StringBuilder textResponse, int maxComponentNameLength, String name,String status,String responseTime) {
        textResponse.append(StringUtils.rightPad(name, maxComponentNameLength));
        textResponse.append(" | ");
        textResponse.append(StringUtils.rightPad(status, 6));
        textResponse.append(" | ");
        textResponse.append(responseTime);
        textResponse.append("\n");
    }
}