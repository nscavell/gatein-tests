package org.gatein.portal.tests.navigation;

import java.util.List;
import java.util.Map;

import com.jayway.restassured.path.json.JsonPath;
import org.gatein.portal.tests.AbstractRestApiTest;
import org.json.JSONArray;
import org.json.JSONObject;
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
    public void update_navigation() throws Exception {
        JsonPath json = given().user("root").expect().statusCode(SC_OK)
                .get("/sites/classic/navigation").jsonPath();

        int priority = json.getInt("priority");

        given().user("root").body("{ \"priority\" : 123 }").expect().statusCode(SC_OK)
                .put("/sites/classic/navigation");

        Thread.sleep(500);

        json = given().user("root").expect().statusCode(SC_OK)
                .get("/sites/classic/navigation").jsonPath();
        assertEquals(123, json.getInt("priority"));

        given().user("root").body("{ \"priority\" : " + priority + " }").expect().statusCode(SC_OK)
                .put("/sites/classic/navigation");
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
    public void node_update() throws Exception {
        String jsonString = given().user("root").expect().statusCode(SC_OK)
                .get("/sites/classic/navigation/home").asString();

        String original = null;
        JSONObject json = new JSONObject(jsonString);
        JSONArray displayNames = json.getJSONArray("displayNames");
        for (int i = 0; i < displayNames.length(); i++) {
            JSONObject displayName = displayNames.getJSONObject(i);
            if ("en".equals(displayName.getString("lang"))) {
                original = displayName.getString("value");
                displayName.put("value", "Home (REST Test)");
            }
        }

        // Update it
        JsonPath jsonPath = given().user("root").body(json.toString()).expect().statusCode(SC_OK)
                .put("/sites/classic/navigation/home").jsonPath();
        List<Map<String, String>> displayName = jsonPath.get("displayNames.findAll { dn -> dn.lang == 'en' }");
        assertEquals("Home (REST Test)", displayName.get(0).get("value"));

        Thread.sleep(300);

        // Ensure it was indeed updated
        jsonPath = given().user("root").expect().statusCode(SC_OK)
                .get("/sites/classic/navigation/home").jsonPath();
        displayName = jsonPath.get("displayNames.findAll { dn -> dn.lang == 'en' }");
        assertEquals("Home (REST Test)", displayName.get(0).get("value"));

        // Now set it back to original
        given().user("root").body(jsonString).expect().statusCode(SC_OK)
                .put("/sites/classic/navigation/home");

        Thread.sleep(300);

        // Ensure it's back to original
        jsonPath = given().user("root").expect().statusCode(SC_OK)
                .get("/sites/classic/navigation/home").jsonPath();
        displayName = jsonPath.get("displayNames.findAll { dn -> dn.lang == 'en' }");
        assertEquals(original, displayName.get(0).get("value"));
    }

    @Test
    public void node_update_bad_data() throws Exception {
        String jsonString = given().user("root").expect().statusCode(SC_OK)
                .get("/sites/classic/navigation/home").asString();

        JSONObject json = new JSONObject(jsonString);
        json.remove("displayNames");
        json.put("displayName", JSONObject.NULL);

        given().user("root").body(json.toString()).expect().statusCode(SC_BAD_REQUEST)
                .put("/sites/classic/navigation/home");
    }

    @Test
    public void anonymous_node_update() throws Exception {
        String jsonString = given().anonymous().expect().statusCode(SC_OK)
                .get("/sites/classic/navigation/home").asString();

        // Anonymous cannot update
        given().anonymous().body(jsonString).expect().statusCode(SC_UNAUTHORIZED)
                .put("/sites/classic/navigation/home");
    }

    @Test
    public void user_node_update() throws Exception {
        String jsonString = given().user("mary").expect().statusCode(SC_OK)
                .get("/sites/classic/navigation/home").asString();

        // User mary cannot update
        given().user("mary").body(jsonString).expect().statusCode(SC_UNAUTHORIZED)
                .put("/sites/classic/navigation/home");
    }

    @Test
    public void node_create_delete() throws Exception {
        // Ensure it doesn't exist
        given().user("root").expect().statusCode(SC_NOT_FOUND)
                .get("/sites/classic/navigation/home/home-1");

        // Create it
        given().user("root").expect().statusCode(SC_OK)
                .post("/sites/classic/navigation/home/home-1");
        Thread.sleep(300);

        // Retrieve it
        given().user("root").expect().statusCode(SC_OK)
                .get("/sites/classic/navigation/home/home-1");

        // Delete it
        given().user("root").expect().statusCode(SC_OK)
                .delete("/sites/classic/navigation/home/home-1");
    }

    @Test
    public void node_create_exists() throws Exception {
        given().user("root").expect().statusCode(SC_CONFLICT)
                .post("/sites/classic/navigation/home");
    }

    @Test
    public void anonymous_node_create() throws Exception {
        given().anonymous().expect().statusCode(SC_UNAUTHORIZED)
                .post("/sites/classic/navigation/home/home-1");
    }

    @Test
    public void anonymous_node_delete() throws Exception {
        given().anonymous().expect().statusCode(SC_UNAUTHORIZED)
                .delete("/sites/classic/navigation/home");
    }
}
