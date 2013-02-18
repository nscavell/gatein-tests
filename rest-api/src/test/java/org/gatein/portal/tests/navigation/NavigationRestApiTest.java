package org.gatein.portal.tests.navigation;

import java.util.List;
import java.util.Map;

import com.jayway.restassured.path.json.JsonPath;
import groovy.json.JsonBuilder;
import org.gatein.portal.tests.AbstractRestApiTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class NavigationRestApiTest extends AbstractRestApiTest {

    @Test
    public void navigation() {
        JsonPath json = given().user("root").expect().statusCode(SC_OK)
                .get("/sites/classic/navigation").jsonPath();

        assertNotNull(json.getString("priority"));
        Integer.parseInt(json.getString("priority")); // Make sure it's an integer
        List<Map<String, String>> nodes = json.getList("nodes");
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());

        // Ensure there are links to the nodes
        for (Map<String, String> node : nodes) {
            assertNotNull(node.get("name"));
            assertNotNull(node.get("url"));
            assertFalse(node.containsKey("children"));
        }
    }

    @Test
    public void navigation_showAll() {
        JsonPath json = given().user("root").expect().statusCode(SC_OK)
                .get("/sites/classic/navigation?showAll=true").jsonPath();

        assertNotNull(json.getString("priority"));
        Integer.parseInt(json.getString("priority")); // Make sure it's an integer
        List<Map<String, String>> nodes = json.getList("nodes");
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());

        boolean found = false;
        for (Map<String, String> node : nodes) {
            if ("notfound".equals(node.get("name"))) {
                found = true;
            }
        }
        assertTrue("notfound system node should have been found", found);
    }

    @Test
    public void navigation_scope() {
        JsonPath json = given().user("root").expect().statusCode(SC_OK)
                .get("/sites/classic/navigation?scope=1").jsonPath();

        assertNotNull(json.getString("priority"));
        Integer.parseInt(json.getString("priority")); // Make sure it's an integer
        List<Map<String, String>> nodes = json.getList("nodes");
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());

        // Ensure the nodes are expanded and displayed
        for (Map<String, String> node : nodes) {
            assertNotNull(node.get("name"));
            assertNotNull(node.get("uri"));
            assertNotNull(node.get("displayName"));
            assertTrue(node.containsKey("children"));
        }
    }

    @Test
    public void anonymous_navigation() {
        JsonPath json = given().anonymous().expect().statusCode(SC_OK)
                .get("/sites/classic/navigation").jsonPath();

        assertNotNull(json.getString("priority"));
        Integer.parseInt(json.getString("priority")); // Make sure it's an integer
        List<Map<String, String>> nodes = json.getList("nodes");
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());

        // Ensure there are links to the nodes
        for (Map<String, String> node : nodes) {
            assertNotNull(node.get("name"));
            assertNotNull(node.get("url"));
            assertFalse(node.containsKey("children"));
        }
    }

    @Test
    public void anonymous_navigation_showAll() {
        JsonPath json = given().anonymous().expect().statusCode(SC_OK)
                .get("/sites/classic/navigation?showAll=true").jsonPath();

        assertNotNull(json.getString("priority"));
        Integer.parseInt(json.getString("priority")); // Make sure it's an integer
        List<Map<String, String>> nodes = json.getList("nodes");
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());

        boolean found = false;
        for (Map<String, String> node : nodes) {
            if ("notfound".equals(node.get("name"))) {
                found = true;
            }
        }
        assertFalse("notfound system node should NOT have been found for anonymous user", found);
    }

    @Test
    public void anonymous_navigation_scope() {
        JsonPath json = given().anonymous().expect().statusCode(SC_OK)
                .get("/sites/classic/navigation?scope=1").jsonPath();

        assertNotNull(json.getString("priority"));
        Integer.parseInt(json.getString("priority")); // Make sure it's an integer
        List<Map<String, String>> nodes = json.getList("nodes");
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());

        // Ensure the nodes are expanded and displayed
        for (Map<String, String> node : nodes) {
            assertNotNull(node.get("name"));
            assertNotNull(node.get("uri"));
            assertNotNull(node.get("displayName"));
            assertTrue(node.containsKey("children"));
        }
    }

    @Test
    public void node() {
        JsonPath json = given().user("root").expect().statusCode(SC_OK)
                .get("/sites/classic/navigation/home").jsonPath();

        assertNotNull(json.getString("name"));
        assertNotNull(json.getString("uri"));
        assertNotNull(json.getString("displayName"));
    }

    @Test
    public void node_scope() {
        JsonPath json = given().user("root").expect().statusCode(SC_OK)
                .get("/spaces/platform/administrators/navigation/administration?scope=1").jsonPath();

        assertNotNull(json.getString("name"));
        assertNotNull(json.getString("uri"));
        assertNotNull(json.getString("displayName"));
        List<Map<String, String>> children = json.getList("children");
        assertNotNull(children);
        assertFalse(children.isEmpty());
        for (Map<String, String> child : children) {
            assertNotNull(child.get("name"));
            assertNotNull(child.get("uri"));
            assertNotNull(child.get("displayName"));
            assertTrue(child.containsKey("children"));
        }
    }

    @Test
    public void anonymous_node() {
        JsonPath json = given().anonymous().expect().statusCode(SC_OK)
                .get("/sites/classic/navigation/home").jsonPath();

        assertNotNull(json.getString("name"));
        assertNotNull(json.getString("uri"));
        assertNotNull(json.getString("displayName"));
    }

    @Test
    public void node_locale() {
        JsonPath json = given().user("root").header("Accept-Language", "fr").expect().statusCode(SC_OK)
                .get("/sites/classic/navigation/home").jsonPath();

        String displayName = json.getString("displayName");
        List<Map<String, String>> displayNames = json.getList("displayNames");
        for (Map<String, String> dn : displayNames) {
            if ("fr".equals(dn.get("lang"))) {
                assertEquals(displayName, dn.get("value"));
            }
        }
    }

    @Test
    public void node_update() {
        JsonPath json = given().user("root").expect().statusCode(SC_OK)
                .get("/sites/classic/navigation/home").jsonPath();

        JsonBuilder jb = new JsonBuilder();
        jb.call(json.getList("displayNames"));
        String displayNames = "{\n   \"displayNames\":" + jb.toPrettyString() + "\n}";

        given().user("root").body(displayNames).expect().statusCode(SC_OK)
                .put("/sites/classic/navigation/home");
    }
}
