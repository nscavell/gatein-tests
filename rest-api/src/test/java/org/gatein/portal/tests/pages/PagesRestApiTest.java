/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.gatein.portal.tests.pages;

import java.util.List;
import java.util.Map;

import com.jayway.restassured.path.json.JsonPath;
import org.gatein.portal.tests.AbstractRestApiTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class PagesRestApiTest extends AbstractRestApiTest {

    //------------------------------- GET (read) requests

    @Test
    public void anonymous_pages() {
        JsonPath json = given().anonymous().expect().statusCode(SC_OK)
                .get("/sites/classic/pages").jsonPath();

        List<Map<String, String>> pages = json.getList("");
        for (Map<String, String> page : pages) {
            assertNotNull(page.get("name"));
            assertEquals("site", page.get("siteType"));
            assertEquals("classic", page.get("siteName"));
            assertNotNull(page.get("url"));
        }
    }

    @Test
    public void pages() {
        JsonPath json = given().user("root").expect().statusCode(SC_OK)
                .get("/sites/classic/pages").jsonPath();

        List<Map<String, String>> pages = json.getList("");
        for (Map<String, String> page : pages) {
            assertNotNull(page.get("name"));
            assertEquals("site", page.get("siteType"));
            assertEquals("classic", page.get("siteName"));
            assertNotNull(page.get("url"));
        }
    }

    @Test
    public void space_pages() {
        JsonPath json = given().user("root").expect().statusCode(SC_OK)
                .get("/spaces/platform/administrators/pages").jsonPath();

        List<Map<String, String>> pages = json.getList("");
        for (Map<String, String> page : pages) {
            assertNotNull(page.get("name"));
            assertEquals("space", page.get("siteType"));
            assertEquals("/platform/administrators", page.get("siteName"));
            assertNotNull(page.get("url"));
        }
    }

    @Test
    public void anonymous_space_pages() {
        given().anonymous().expect().statusCode(SC_UNAUTHORIZED)
                .get("/spaces/platform/administrators/pages");
    }

    @Test
    public void pages_not_found() {
        given().anonymous().expect().statusCode(SC_NOT_FOUND)
                .get("/sites/foo/pages");
        given().user("root").expect().statusCode(SC_NOT_FOUND)
                .get("/sites/foo/pages");
    }

    @Test
    public void anonymous_page() {
        JsonPath json = given().anonymous().expect().statusCode(SC_OK)
                .get("/sites/classic/pages/homepage").jsonPath();

        assertNotNull(json.get("name"));
        assertNotNull(json.get("displayName"));
        assertNotNull(json.get("access-permissions"));
        assertNotNull(json.get("edit-permissions"));
    }

    @Test
    public void page() {
        JsonPath json = given().user("root").expect().statusCode(SC_OK)
                .get("/sites/classic/pages/homepage").jsonPath();

        assertNotNull(json.get("name"));
        assertNotNull(json.get("displayName"));
        assertNotNull(json.get("access-permissions"));
        assertNotNull(json.get("edit-permissions"));
    }

    @Test
    public void space_page() {
        JsonPath json = given().user("root").expect().statusCode(SC_OK)
                .get("/spaces/platform/administrators/pages/registry").jsonPath();

        assertNotNull(json.get("name"));
        assertNotNull(json.get("displayName"));
        assertNotNull(json.get("access-permissions"));
        assertNotNull(json.get("edit-permissions"));
    }

    @Test
    public void anonymous_space_page() {
        given().anonymous().expect().statusCode(SC_UNAUTHORIZED)
                .get("/spaces/platform/administrators/pages/registry");
    }

    @Test
    public void page_not_found() {
        given().anonymous().expect().statusCode(SC_NOT_FOUND)
                .get("/sites/classic/pages/bar");
        given().user("root").expect().statusCode(SC_NOT_FOUND)
                .get("/sites/classic/pages/bar");
    }

    //------------------------------- POST (create) requests

    @Test
    public void anonymous_create_page() {
        given().anonymous().expect().statusCode(SC_UNAUTHORIZED)
                .post("/sites/classic/pages/newpage");
    }

    @Test
    public void create_page() throws Exception {
        // Create the page
        given().user("root").expect().statusCode(SC_OK)
                .post("/sites/classic/pages/newpage");
        Thread.sleep(300);

        // Verify root has access
        given().user("root").expect().statusCode(SC_OK)
                .get("/sites/classic/pages/newpage");

        // Verify anonymous has access (default permissions when creating page)
        given().anonymous().expect().statusCode(SC_OK)
                .get("/sites/classic/pages/newpage");

        given().user("root").expect().statusCode(SC_OK)
                .delete("/sites/classic/pages/newpage");
    }

    @Test
    public void create_page_exists() throws Exception {
        given().user("root").expect().statusCode(SC_CONFLICT)
                .post("/sites/classic/pages/homepage");
    }

    //------------------------------- PUT (update) requests

    @Test
    public void anonymous_update_page() {
        given().anonymous().expect().statusCode(SC_UNAUTHORIZED)
                .put("/sites/classic/pages/homepage");
    }

    @Test
    public void update_page() {
        // Retrieve the displayName so we can set back after test
        JsonPath json = given().user("root").expect().statusCode(SC_OK)
                .get("/sites/classic/pages/homepage").jsonPath();

        String displayName = json.getString("displayName");
        assertNotNull(displayName);

        // Update displayName of home page
        json =  given().user("root").body("{\"displayName\":\"Test\"}").expect().statusCode(SC_OK)
                .put("/sites/classic/pages/homepage").jsonPath();
        assertEquals("Test", json.getString("displayName"));

        // Verify it was updated by fetching it again
        json = given().user("root").expect().statusCode(SC_OK)
                .get("/sites/classic/pages/homepage").jsonPath();
        assertEquals("Test", json.getString("displayName"));

        // Verify anonymous can see update as well
        json = given().anonymous().expect().statusCode(SC_OK)
                .get("/sites/classic/pages/homepage").jsonPath();
        assertEquals("Test", json.getString("displayName"));

        // Now set the displayName of home page back to original
        json =  given().user("root").body("{\"displayName\":\""+displayName+"\"}").expect().statusCode(SC_OK)
                .put("/sites/classic/pages/homepage").jsonPath();
        assertEquals(displayName, json.getString("displayName"));
    }
}
