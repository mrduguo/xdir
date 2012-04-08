package org.duguo.xdir.infra.http.rest.impl.provider;


import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
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
public class PingOkTextMessageBodyWriter implements MessageBodyWriter<PingOk> {
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return PingOk.class.isAssignableFrom(type);
    }
    
    public long getSize(PingOk obj, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return obj.getStatus().length();
    }

    public void writeTo(PingOk obj, Class type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        entityStream.write(obj.getStatus().getBytes());
    }
}