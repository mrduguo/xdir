package org.duguo.xdir.infra.http.rest.api.resource;

import org.duguo.xdir.infra.http.rest.api.model.ComponentInfo;
import org.duguo.xdir.infra.http.rest.api.model.ComponentsInfo;
import org.duguo.xdir.infra.http.rest.api.model.PingOk;
import org.duguo.xdir.infra.http.rest.api.model.ServerInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;


/**
 * server info resource
 */
@Path("v1/server")
public interface ServerResource {

    /**
     *
     * Retrieve the server info
     */
    @Path("info")
    @GET
    @Produces({"application/json","application/xml"})
    public ServerInfo info() ;

    /**
     *
     * Retrieve the components info.
     *
     * This is expensive operation and should not be called frequently.
     *
     * Return status code OK - 200 if all components is OK, otherwise return  PARTIAL_CONTENT - 206
     */
    @Path("components")
    @GET
    @Produces({"text/plain","application/json","application/xml"})
    public ComponentsInfo components() ;

    /**
     *
     * Ping the server to make sure it's up
     */
    @Path("ping")
    @GET
    @Produces({"text/plain","application/json","application/xml"})
    public PingOk ping() ;
}