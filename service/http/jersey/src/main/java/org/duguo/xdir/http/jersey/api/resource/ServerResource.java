package org.duguo.xdir.http.jersey.api.resource;

import org.duguo.xdir.http.jersey.api.model.ServerInfo;
import org.duguo.xdir.spi.security.Secure;

import javax.ws.rs.*;


/**
 * server info resource
 */
@Path("server")
public interface ServerResource {

    /**
     *
     * retrieve the server info
     */
    @Path("info")
    @GET
    @Produces({"application/json","application/xml","text/plain"})
    @Secure
    public ServerInfo info(@QueryParam("client") String client) ;

    /**
     *
     * ping the server to make sure it's up
     */
    @Path("ping")
    @GET
    @Produces("text/plain")
    public String ping() ;
}