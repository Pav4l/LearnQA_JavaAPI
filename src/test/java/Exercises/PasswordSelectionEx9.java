package Exercises;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PasswordSelectionEx9 {

    @Test
    public void testBruteForcePassword() {
        List<String> passwords = Arrays.asList(
                "123456", "password", "123456789", "12345678", "12345", "1234567", "1234567890", "qwerty", "abc123",
                "111111", "123123", "admin", "letmein", "welcome", "monkey", "dragon", "password1", "123qwe", "123456a",
                "654321", "superman", "batman", "trustno1", "sunshine", "iloveyou"
        );

        String login = "super_admin";

        for (String password : passwords) {
            Map<String, String> data = new HashMap<>();
            data.put("login", login);
            data.put("password", password);

            Response responseForGet = RestAssured
                    .given()
                    .body(data)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();

            String authCookie = responseForGet.getCookie("auth_cookie");

            Map<String, String> cookies = new HashMap<>();
            cookies.put("auth_cookie", authCookie);

            Response responseForCheck = RestAssured
                    .given()
                    .cookies(cookies)
                    .when()
                    .get("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                    .andReturn();

            String responseMessage = responseForCheck.getBody().asString();
            if (!responseMessage.equals("You are NOT authorized")) {
                System.out.println("Correct password: " + password);
                System.out.println("Response: " + responseMessage);
                break;
            }
        }
    }
}