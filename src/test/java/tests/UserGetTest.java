package tests;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.BaseTestCase;
import lib.ApiCoreRequests;
import lib.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("User retrieval")
@Feature("Get user data")
public class UserGetTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    @DisplayName("Test retrieving user data without authentication")
    @Description("This test verifies retrieving user data without authentication")
    @Severity(SeverityLevel.MINOR)
    @Issue("PROJECT-130")
    public void testGetUserDataNotAuth(){
    Response responseUserData = RestAssured
            .get("https://playground.learnqa.ru/api/user/2")
            .andReturn();

    Assertions.assertJsonHasField(responseUserData,"username");
    Assertions.assertJsonHasNotField(responseUserData,"firstname");
    Assertions.assertJsonHasNotField(responseUserData,"lastname");
    Assertions.assertJsonHasNotField(responseUserData,"email");
    }

    @Test
    @DisplayName("Test retrieving user details as same authenticated user")
    @Description("This test verifies retrieving user details as the authenticated user")
    @Severity(SeverityLevel.NORMAL)
    @Issue("PROJECT-131")
    public void testGetUserDetailsAuthAsSameUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email","vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

    @Test
    @DisplayName("Test retrieving user details as another authenticated user")
    @Description("This test verifies retrieving user details as another authenticated user")
    @Severity(SeverityLevel.CRITICAL)
    @Issue("PROJECT-132")
    public void testGetUserDetailsAuthAsAnotherUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/1", header, cookie);

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }
}