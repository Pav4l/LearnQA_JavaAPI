package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    public void testEditJustCreatedTest(){
        //GENERATE USER
        Map<String,String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();

        String userId = responseCreateAuth.getString("id");


        //LOGIN
        Map<String,String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //EDIT
        String newName = "Changed Name";
        Map<String, String>  editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth,"x-csrf-token"))
                .cookie("auth_sid",this.getCookie(responseGetAuth,"auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //GET
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token",this.getHeader(responseGetAuth,"x-csrf-token"))
                .cookie("auth_sid",this.getCookie(responseGetAuth,"auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        Assertions.assertJsonByName(responseUserData,"firstName", newName);
    }

    @Test
    public void testEditUserNotAuth() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateUser = apiCoreRequests.makePostRequestCreateUser(
                "https://playground.learnqa.ru/api/user/",
                userData);

        String userId = responseCreateUser.jsonPath().getString("id");

        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                null,
                null,
                editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "{\"error\":\"Auth token not supplied\"}");
    }

    @Test
    public void testEditUserAuthAsAnotherUser() {
        Map<String, String> userData1 = DataGenerator.getRegistrationData();
        Response responseCreateUser1 = apiCoreRequests.makePostRequestCreateUser(
                "https://playground.learnqa.ru/api/user/",
                userData1);

        String userId1 = responseCreateUser1.jsonPath().getString("id");


        Map<String, String> userData2 = DataGenerator.getRegistrationData();
        Response responseCreateUser2 = apiCoreRequests.makePostRequestCreateUser(
                "https://playground.learnqa.ru/api/user/",
                userData2);

        String email2 = userData2.get("email");
        String password2 = userData2.get("password");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", email2);
        authData.put("password", password2);

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + userId1,
                header,
                cookie,
                editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
    }

    @Test
    public void testEditUserEmailToInvalid() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateUser = apiCoreRequests.makePostRequestCreateUser(
                "https://playground.learnqa.ru/api/user/",
                userData);

        String email = userData.get("email");
        String password = userData.get("password");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", email);
        authData.put("password", password);

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        String invalidEmail = "invalidemail.com";
        Map<String, String> editData = new HashMap<>();
        editData.put("email", invalidEmail);

        Response responseEditUser = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + responseCreateUser.jsonPath().getString("id"),
                header,
                cookie,
                editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "{\"error\":\"Invalid email format\"}");
    }

    @Test
    public void testEditUserFirstNameToShort() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateUser = apiCoreRequests.makePostRequestCreateUser(
                "https://playground.learnqa.ru/api/user/",
                userData);

        String email = userData.get("email");
        String password = userData.get("password");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", email);
        authData.put("password", password);

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        String shortFirstName = "A";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", shortFirstName);

        Response responseEditUser = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api/user/" + responseCreateUser.jsonPath().getString("id"),
                header,
                cookie,
                editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "{\"error\":\"The value for field `firstName` is too short\"}");
    }
}
