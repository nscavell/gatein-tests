package org.gatein.portal.tests.sites;

import com.jayway.restassured.path.json.JsonPath;
import org.gatein.portal.tests.AbstractRestApiTest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class SitesRestApiTest extends AbstractRestApiTest {

    @Test
    public void anonymous_sites() {
        String response = given().anonymous().expect().statusCode(200).get("/sites").asString();
        JsonPath json = new JsonPath(response);

        List<String> knownSites = new ArrayList<String>(Arrays.asList("classic", "mobile"));
        List<Map<String, String>> sites = json.getList("");
        assertEquals(knownSites.size(), sites.size());
        for (Map<String, String> site : sites) {
            assertEquals("site", site.get("type"));
            assertTrue(knownSites.contains(site.get("name")));
            knownSites.remove(site.get("name"));
            assertNotNull(site.get("url"));
        }
    }

    @Test
    public void anonymous_spaces() {
        String response = given().anonymous().expect().statusCode(200).get("/spaces").asString();
        JsonPath json = new JsonPath(response);

        List<String> knownSites = new ArrayList<String>(Arrays.asList("/platform/guests"));
        List<Map<String, String>> sites = json.getList("");
        assertEquals(knownSites.size(), sites.size());
        for (Map<String, String> site : sites) {
            assertEquals("space", site.get("type"));
            assertTrue(knownSites.contains(site.get("name")));
            knownSites.remove(site.get("name"));
            assertNotNull(site.get("url"));
        }
    }

    @Test
    public void root_sites() {
        String response = given().user("root").expect().statusCode(200).get("/sites").asString();
        JsonPath json = new JsonPath(response);

        List<String> knownSites = new ArrayList<String>(Arrays.asList("classic", "mobile"));
        List<Map<String, String>> sites = json.getList("");
        assertEquals(knownSites.size(), sites.size());
        for (Map<String, String> site : sites) {
            assertEquals("site", site.get("type"));
            assertTrue(knownSites.contains(site.get("name")));
            knownSites.remove(site.get("name"));
            assertNotNull(site.get("url"));
        }
    }

    @Test
    public void root_spaces() {
        String response = given().user("root").expect().statusCode(200).get("/spaces").asString();
        JsonPath json = new JsonPath(response);

        List<String> knownSites = new ArrayList<String>(Arrays.asList("/platform/guests", "/organization/management/executive-board", "/platform/administrators", "/platform/users"));
        List<Map<String, String>> sites = json.getList("");
        assertEquals(knownSites.size(), sites.size());
        for (Map<String, String> site : sites) {
            assertEquals("space", site.get("type"));
            assertTrue(knownSites.contains(site.get("name")));
            knownSites.remove(site.get("name"));
            assertNotNull(site.get("url"));
        }
    }

    //TODO: Find way to test dashboards, they aren't created until the user logs into the portal
}
