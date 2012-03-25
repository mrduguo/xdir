package org.duguo.xdir.http.jersey.api.resource;

import org.duguo.xdir.http.jersey.api.model.ServerInfo;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


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
    public ServerInfo info() ;

    /**
     *
     * ping the server to make sure it's up
     */
    @Path("ping")
    @GET
    @Produces("text/plain")
    public String ping() ;
}