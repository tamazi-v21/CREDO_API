package com.credo.tests;

import io.restassured.response.Response;
import com.credo.client.UsersApiClient;
import com.credo.model.User;
import com.credo.wiremock.UserStubs;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static com.credo.wiremock.UserStubs.ALICE;
import static com.credo.wiremock.UserStubs.BOB;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class UsersApiTest {

    private final UsersApiClient api = new UsersApiClient();

    @BeforeClass(alwaysRun = true)
    public void loadStubs() {
        UserStubs.register();
    }

    @DataProvider
    public Object[][] allUsersData() {
        return new Object[][]{{List.of(ALICE, BOB)}};
    }

    @Test(dataProvider = "allUsersData")
    public void testGetAllUsers_Positive(List<User> expected) {
        Response response = api.getUsers();

        assertEquals(response.statusCode(), 200);
        assertTrue(response.getContentType().contains("application/json"));
        assertEquals(users(response), expected);
    }

    @DataProvider
    public Object[][] ageFilterData() {
        return new Object[][]{
                {30, ALICE},
                {25, BOB}
        };
    }

    @Test(dataProvider = "ageFilterData")
    public void testFilterByAge_Positive(int age, User expected) {
        Response response = api.getUsers(Map.of("age", age));

        assertEquals(response.statusCode(), 200);
        assertEquals(users(response), List.of(expected));
    }

    @DataProvider
    public Object[][] genderFilterData() {
        return new Object[][]{
                {"male", BOB},
                {"female", ALICE}
        };
    }

    @Test(dataProvider = "genderFilterData")
    public void testFilterByGender_Positive(String gender, User expected) {
        Response response = api.getUsers(Map.of("gender", gender));

        assertEquals(response.statusCode(), 200);
        assertEquals(users(response), List.of(expected));
    }

    @DataProvider
    public Object[][] invalidAgeData() {
        return new Object[][]{{"-1"}};
    }

    @Test(dataProvider = "invalidAgeData")
    public void testInvalidAge_Negative(String age) {
        assertEquals(api.getUsers(Map.of("age", age)).statusCode(), 400);
    }

    @DataProvider
    public Object[][] invalidGenderData() {
        return new Object[][]{{"unknown"}};
    }

    @Test(dataProvider = "invalidGenderData")
    public void testInvalidGender_Negative(String gender) {
        Response response = api.getUsers(Map.of("gender", gender));

        assertEquals(response.statusCode(), 422);
        assertTrue(users(response).isEmpty());
    }

    @DataProvider
    public Object[][] serverErrorData() {
        return new Object[][]{{500}};
    }

    @Test(dataProvider = "serverErrorData")
    public void testInternalServerError_Negative(int expectedStatus) {
        UserStubs.overrideAllUsersWith500();
        try {
            assertEquals(api.getUsers().statusCode(), expectedStatus);
        } finally {
            UserStubs.register();
        }
    }

    private static List<User> users(Response response) {
        return response.jsonPath().getList(".", User.class);
    }
}