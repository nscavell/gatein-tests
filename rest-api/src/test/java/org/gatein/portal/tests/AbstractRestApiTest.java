package org.gatein.portal.tests;

import com.jayway.restassured.path.json.JsonPath;
import org.gatein.portal.tests.spec.RestApiRequestSpecification;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public abstract class AbstractRestApiTest {

    public static RestApiRequestSpecification given() {
        return new RestApiRequestSpecification();
    }

    protected static void assertList(JsonPath json, String list, String field, String value) {
        List<Map<String, ?>> result = json.get(list + ".findAll { item -> item."+field+" == '"+value+"'}");
        assertEquals(1, result.size());
    }
}
