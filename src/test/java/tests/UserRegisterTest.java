package tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Test creating user with existing email")
    @Description("This test verifies behavior when trying to create a user with an existing email")
    @Severity(SeverityLevel.NORMAL)
    @Issue("PROJECT-123")
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
    @DisplayName("Test creating user successfully")
    @Description("This test verifies successful creation of a user")
    @Severity(SeverityLevel.CRITICAL)
    @Issue("PROJECT-124")
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
    @DisplayName("Test creating user with invalid email format")
    @Description("This test verifies behavior when trying to create a user with an invalid email format")
    @Severity(SeverityLevel.MINOR)
    @Issue("PROJECT-125")
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
    @DisplayName("Test creating user without required field")
    @Description("This test verifies behavior when trying to create a user without a required field")
    @Severity(SeverityLevel.BLOCKER)
    @Issue("PROJECT-126")
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
    @DisplayName("Test creating user with too short first name")
    @Description("This test verifies behavior when trying to create a user with a too short first name")
    @Severity(SeverityLevel.NORMAL)
    @Issue("PROJECT-127")
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
    @DisplayName("Test creating user with too long first name")
    @Description("This test verifies behavior when trying to create a user with a too long first name")
    @Severity(SeverityLevel.NORMAL)
    @Issue("PROJECT-128")
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
