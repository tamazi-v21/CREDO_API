package com.credo.client;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import com.credo.config.TestConfig;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class UsersApiClient {

    private final RequestSpecification spec = new RequestSpecBuilder()
            .setBaseUri(TestConfig.BASE_URI)
            .setPort(TestConfig.WIREMOCK_PORT)
            .setBasePath(TestConfig.USERS_PATH)
            .build();

    public Response getUsers() {
        return getUsers(Map.of());
    }

    public Response getUsers(Map<String, ?> queryParams) {
        return given().spec(spec)
                .queryParams(queryParams)
                .when().get()
                .then().extract().response();
    }
}
