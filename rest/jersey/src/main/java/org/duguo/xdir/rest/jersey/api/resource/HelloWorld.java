package org.duguo.xdir.rest.jersey.api.resource;

import org.duguo.xdir.rest.jersey.api.model.Item;

import javax.ws.rs.*;


/**
 * Hello world test resource
 */
@Path("/demo/helloworld/{itemName}")
public interface HelloWorld {


    /**
     * getInfo the item as string
     * @response.representation.200.example ["myValue"]
     * @return item as string
     *
     */
    @GET()
    @Produces("text/html")
    public String getStringItem();

    /**
     *
     * retrieve the item with xml/json format
     * <p>
     * <a href="test">testing</a>
     *    </p>
     * @param itemName  item test name
     * @param itemValue item test value
     * @return the item object
     */
    @GET
    @Produces({"application/xml","application/json"})
    public Item getItem(@PathParam("itemName") String itemName, @QueryParam("itemValue") String itemValue) ;
}