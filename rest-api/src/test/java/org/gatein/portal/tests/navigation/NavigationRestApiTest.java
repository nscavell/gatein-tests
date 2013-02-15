package org.gatein.portal.tests.navigation;

import com.jayway.restassured.path.json.JsonPath;
import groovy.json.JsonBuilder;
import org.gatein.portal.tests.AbstractRestApiTest;
import org.junit.Test;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class NavigationRestApiTest extends AbstractRestApiTest {

    @Test
    public void navigation() {
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
