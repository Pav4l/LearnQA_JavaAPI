package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("User registration cases")
@Feature("Create user")
public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    public void testCreateUserWithExistingEmail(){
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/ajax/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"Users with email '" + email + "' already exists");

    }

    @Test
    public void testCreateUserSuccessfully(){
        String email = DataGenerator.getRandomEmail();

        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/ajax/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth,"id");
    }

    @Test
    public void testCreateUserWithInvalidEmail(){
        String email = "vinkotovexample.com";

        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("email", email);

        Response responseCreateAuth = apiCoreRequests.makePostRequestCreateUser(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }

    @ParameterizedTest
    @ValueSource(strings = {"username",
            "firstName",
            "lastName",
            "email",
            "password"})
    public void testCreateUserWithoutRequiredField(String field) {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.remove(field);

        Response responseCreateAuth = apiCoreRequests.makePostRequestCreateUser(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: " + field);
    }

    @Test
    public void testCreateUserWithShortName(){
        String shortName = DataGenerator.generateRandomString(1);

        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("firstName", shortName);

        Response responseCreateAuth = apiCoreRequests.makePostRequestCreateUser(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'firstName' field is too short");
    }

    @Test
    public void testCreateUserWithLongName(){
        String longName = DataGenerator.generateRandomString(251);

        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("firstName", longName);

        Response responseCreateAuth = apiCoreRequests.makePostRequestCreateUser(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'firstName' field is too long");
    }
}
