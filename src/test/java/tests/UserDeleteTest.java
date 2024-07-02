package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.BaseTestCase;
import lib.Assertions;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;

@Epic("User Deletion")
@Feature("DELETE User")
public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("This test attempts to delete user with ID 2 and expects failure")
    @DisplayName("Negative: Delete user with ID 2")
    public void testDeleteUserWithId2() {
        //Login as user with ID 2
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response loginResponse = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);
        String token = this.getHeader(loginResponse, "x-csrf-token");
        String cookie = this.getCookie(loginResponse, "auth_sid");

        //Attempt to delete user with ID 2
        Response deleteResponse = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/2",
                token,
                cookie);

        Assertions.assertResponseCodeEquals(deleteResponse, 400);
        Assertions.assertResponseTextEquals(
                deleteResponse,
                "{\"error\":\"Please, do not delete test users with ID 1, 2, 3, 4 or 5.\"}");
    }

    @Test
    @Description("This test creates and deletes a new user and verifies the deletion")
    @DisplayName("Positive: Create and delete user")
    public void testCreateAndDeleteUser() {
        //Create user
        Map<String, String> userData = new HashMap<>();
        userData.put("email", "newuser@example.com");
        userData.put("password", "1234");
        userData.put("username", "newuser");
        userData.put("firstName", "New");
        userData.put("lastName", "User");

        Response createResponse = apiCoreRequests.makePostRequestCreateUser(
                "https://playground.learnqa.ru/api/user",
                userData);
        int userId = createResponse.jsonPath().getInt("id");

        //Login as new user
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "newuser@example.com");
        authData.put("password", "1234");

        Response loginResponse = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);
        String token = this.getHeader(loginResponse, "x-csrf-token");
        String cookie = this.getCookie(loginResponse, "auth_sid");

        //Delete new user
        Response deleteResponse = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                token,
                cookie);
        Assertions.assertResponseCodeEquals(deleteResponse, 200);

        //Verify user is deleted
        Response getUserResponse = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                token,
                cookie);
        Assertions.assertResponseCodeEquals(getUserResponse, 404);
        Assertions.assertResponseTextEquals(getUserResponse, "User not found");
    }

    @Test
    @Description("This test removes a user who is logged in as another user")
    @DisplayName("Negative: Delete another user")
    public void testEditAnotherUser() {

        //Generate user
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateUser = apiCoreRequests
                .makePostRequestCreateUser(
                        "https://playground.learnqa.ru/api/user/",
                        userData);

        String userId = responseCreateUser.jsonPath().get("id");

        //Login as another user
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");
        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String cookie = this.getCookie(responseGetAuth, "auth_sid");
        String token = this.getHeader(responseGetAuth, "x-csrf-token");

        //Delete as user
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/" + userId,
                        token,
                        cookie);
        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertResponseTextEquals(responseDeleteUser,
                "{\"error\":\"Please, do not delete test users with ID 1, 2, 3, 4 or 5.\"}");
    }
}
