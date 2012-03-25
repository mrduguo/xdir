package org.duguo.xdir.http.jersey.api.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RuntimeExceptionMapper  implements ExceptionMapper<RuntimeException> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Response toResponse(RuntimeException ex) {
        if(ex instanceof WebApplicationException){
             return ((WebApplicationException)ex).getResponse();
        }else{
            String errorMsg="ERROR_UID: "+System.currentTimeMillis()+" EXCEPTION: "+ex.getMessage();
            logger.error(errorMsg,ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMsg).build();
        }
    }

}