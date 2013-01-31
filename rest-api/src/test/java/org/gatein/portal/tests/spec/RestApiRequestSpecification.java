package org.gatein.portal.tests.spec;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.specification.RequestSpecification;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class RestApiRequestSpecification {
    public static String basePath = "/rest/managed-components/api";
    public static String baseAuthPath = "/rest/private/managed-components/api";

    public static String password = "gtn";
    public static String contentType = "application/json";

    public RequestSpecification user(String userName) {
        return user(userName, System.getProperty("password", password));
    }

    public RequestSpecification user(String user, String password) {
        RestAssured.basePath = System.getProperty("baseAuthPath", baseAuthPath);
        return build(RestAssured.given().auth().basic(user, password));
    }

    public RequestSpecification anonymous() {
        RestAssured.basePath = System.getProperty("basePath", basePath);
        return build(RestAssured.given());
    }

    private RequestSpecification build(RequestSpecification spec) {
        return spec.header("Accept", contentType).header("Content-Type", contentType);
    }
}
