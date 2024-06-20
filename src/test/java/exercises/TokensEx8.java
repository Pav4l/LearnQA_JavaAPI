package exercises;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class TokensEx8 {

    @Test
    public void testLongTimeJob() throws InterruptedException {
        Response createResponse = RestAssured
                .given()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .andReturn();

        createResponse.prettyPrint();

        int seconds = createResponse.jsonPath().getInt("seconds");
        String token = createResponse.jsonPath().getString("token");

        Response beforeReadyResponse = RestAssured
                .given()
                .queryParam("token", token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .andReturn();

        beforeReadyResponse.prettyPrint();

        String beforeStatus = beforeReadyResponse.jsonPath().getString("status");

        Thread.sleep(seconds * 1000);

        Response afterReadyResponse = RestAssured
                .given()
                .queryParam("token", token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .andReturn();

        afterReadyResponse.prettyPrint();

        String afterStatus = afterReadyResponse.jsonPath().getString("status");
        String result = afterReadyResponse.jsonPath().getString("result");
    }
}