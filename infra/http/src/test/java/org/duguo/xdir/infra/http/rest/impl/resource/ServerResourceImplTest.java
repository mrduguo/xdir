package org.duguo.xdir.infra.http.rest.impl.resource;

import com.jayway.restassured.http.ContentType;
import org.apache.http.StatusLine;
import org.duguo.xdir.infra.test.spring.AbstractTestContextAwareTest;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.metal.MetalBorders;
import javax.ws.rs.core.MediaType;

import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

public class ServerResourceImplTest extends AbstractTestContextAwareTest {

    @Test
    public void serverInfoShouldSupportXmlAndJsonMediaType() throws Exception {
        expect().statusCode(HttpServletResponse.SC_OK).body("upTime", containsString("seconds")).contentType(MediaType.APPLICATION_JSON).
                when().get("/rest/v1/server/info.json");

        expect().statusCode(HttpServletResponse.SC_OK).body("server.upTime", containsString("seconds")).contentType(MediaType.APPLICATION_XML).
                when().get("/rest/v1/server/info.xml");
    }

    @Test
    public void pingShouldReturnStatus200WithTextOk() throws Exception {
        expect().statusCode(HttpServletResponse.SC_OK).content(equalTo("OK")).contentType(MediaType.TEXT_PLAIN).
                when().get("/rest/v1/server/ping.txt");
        Thread.sleep(Long.MAX_VALUE);
    }

}
