package org.duguo.xdir.rest.jersey.api.resource;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


/**
 * Hello world test resource
 */
@Path("/account")
public interface AccountResource {


    /**
     * Authenticate user and set auth cookie if success
     */
    @POST
    @Path("/auth")
    @Produces("application/json")
    public String auth(@FormParam("loginName") String loginName,@FormParam("credential") String credential);


    /**
     * Register a new user account
     */
    @POST
    @Path("/register")
    @Produces("application/json")
    public String register(@FormParam("loginName") String loginName,@FormParam("credential") String credential,@FormParam("email") String email);

}