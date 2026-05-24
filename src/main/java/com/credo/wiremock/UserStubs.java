package com.credo.wiremock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.credo.config.TestConfig;
import com.credo.model.User;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public final class UserStubs {

    public static final User ALICE = User.builder().id(1).name("Alice").age(30).gender("female").build();
    public static final User BOB = User.builder().id(2).name("Bob").age(25).gender("male").build();

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private UserStubs() {
    }

    public static void register() {
        try {
            WireMock.configureFor(TestConfig.WIREMOCK_HOST, TestConfig.WIREMOCK_PORT);
            WireMock.removeAllMappings();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "WireMock not reachable at " + TestConfig.WIREMOCK_HOST + ":" + TestConfig.WIREMOCK_PORT, e);
        }

        stubFor(filter("age", "30").willReturn(json(200, List.of(ALICE))));
        stubFor(filter("age", "25").willReturn(json(200, List.of(BOB))));
        stubFor(filter("gender", "female").willReturn(json(200, List.of(ALICE))));
        stubFor(filter("gender", "male").willReturn(json(200, List.of(BOB))));

        stubFor(users().atPriority(1).withQueryParam("age", matching("-\\d+|0|.*[^0-9].*"))
                .willReturn(error(400, "Bad Request", "age must be a positive integer")));

        stubFor(users().atPriority(3).withQueryParam("gender", matching(".+"))
                .willReturn(json(422, List.of())));

        stubFor(users().atPriority(5).willReturn(json(200, List.of(ALICE, BOB))));
    }

    public static void overrideAllUsersWith500() {
        WireMock.removeAllMappings();
        stubFor(users().willReturn(error(500, "Internal Server Error", "unexpected error")));
    }

    private static MappingBuilder users() {
        return get(urlPathEqualTo(TestConfig.USERS_PATH));
    }
    private static MappingBuilder filter(String param, String value) {
        return users().atPriority(2).withQueryParam(param, equalTo(value));
    }

    private static ResponseDefinitionBuilder json(int status, Object body) {
        return aResponse()
                .withStatus(status)
                .withHeader("Content-Type", "application/json")
                .withBody(toJson(body));
    }

    private static ResponseDefinitionBuilder error(int status, String error, String message) {
        return json(status, Map.of("status", status, "error", error, "message", message));
    }

    private static String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize stub body", e);
        }
    }
}
