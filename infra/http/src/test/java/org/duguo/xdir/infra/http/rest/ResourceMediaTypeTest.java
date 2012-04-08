package org.duguo.xdir.infra.http.rest;

import org.duguo.xdir.infra.test.spring.AbstractTestContextAwareTest;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class ResourceMediaTypeTest extends AbstractTestContextAwareTest {


    @Test
    public void mediaTypeCouldPassAsAcceptHeader() throws Exception {
        expect().statusCode(HttpServletResponse.SC_OK).contentType(MediaType.APPLICATION_XML).
                given().header("Accept", MediaType.APPLICATION_XML).
                when().get("/rest/v1/server/ping");

        expect().statusCode(HttpServletResponse.SC_OK).contentType(MediaType.APPLICATION_JSON).
                given().header("Accept", MediaType.APPLICATION_JSON).
                when().get("/rest/v1/server/ping");
    }

    @Test
    public void extensionShouldOverrideAcceptHeaderWhenBothProvided() throws Exception {
        expect().statusCode(HttpServletResponse.SC_OK).contentType(MediaType.APPLICATION_JSON).
                given().header("Accept", MediaType.APPLICATION_XML).
                when().get("/rest/v1/server/ping.json");
    }

    @Test
    public void defaultMediaTypeShouldDecidedByServer() throws Exception {
        expect().statusCode(HttpServletResponse.SC_OK).contentType(MediaType.TEXT_PLAIN).
                when().get("/rest/v1/server/ping");

        expect().statusCode(HttpServletResponse.SC_OK).contentType(MediaType.APPLICATION_JSON).
                when().get("/rest/v1/server/ping");
    }

    @Test
    public void extensionShouldReflectMediaType() throws Exception {
        expect().statusCode(HttpServletResponse.SC_OK).contentType(MediaType.APPLICATION_JSON).
                when().get("/rest/v1/server/ping.json");

        expect().statusCode(HttpServletResponse.SC_OK).contentType(MediaType.APPLICATION_XML).
                when().get("/rest/v1/server/ping.xml");

        expect().statusCode(HttpServletResponse.SC_OK).contentType(MediaType.TEXT_PLAIN).
                when().get("/rest/v1/server/ping.txt");
    }

    /*
     ===========================
       NEGATIVE TESTS
     ===========================
     */

    @Test
    public void jaxbObjectShouldNotAcceptTextPlainMediaType() throws Exception {
        expect().statusCode(HttpServletResponse.SC_NOT_ACCEPTABLE).
                when().get("/rest/v1/server/info.txt");
    }

    @Test
    public void unknownExtensionWillResultResourceNotFound() throws Exception {
        expect().statusCode(HttpServletResponse.SC_NOT_FOUND).
                when().get("/rest/v1/server/info.xyz");
    }

}
