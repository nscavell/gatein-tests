package org.gatein.portal.tests;

import com.jayway.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
//TODO: Write a test that just follow links
public class RestApiTest extends AbstractRestApiTest {

    // Tests for REST API entry point .../api

    @Test
    public void anonymous() {
        JsonPath json = given().anonymous().expect().statusCode(SC_OK).when()
            .get("/").jsonPath();

        assertChild(json, "sites");
        assertChild(json, "spaces");
        assertChild(json, "dashboards");
    }

    @Test
    public void anonymous_unauthorized() {
        given().anonymous().expect().statusCode(SC_UNAUTHORIZED);
    }

    @Test
    public void root() {
        JsonPath json = given().user("root").expect().statusCode(SC_OK).when()
            .get("/").jsonPath();

        assertChild(json, "sites");
        assertChild(json, "spaces");
        assertChild(json, "dashboards");
    }

    private static void assertChild(JsonPath json, String childName) {
        List<Map<String, ?>> result = json.get("children.findAll { child -> child.name == '"+childName+"'}");
        assertEquals(1, result.size());
    }
}
