package org.example.config;

public final class TestConfig {

    public static final String WIREMOCK_HOST = System.getProperty("wiremock.host", "localhost");
    public static final int WIREMOCK_PORT = Integer.parseInt(System.getProperty("wiremock.port", "8080"));
    public static final String BASE_URI = "http://" + WIREMOCK_HOST;

    public static final String USERS_PATH = "/users";

    public static final String DB_URL = "jdbc:sqlite:" + System.getProperty("db.path", "test-results.db");
}