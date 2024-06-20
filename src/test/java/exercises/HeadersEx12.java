package exercises;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HeadersEx12 {
    @Test
    public void testHeader(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        Headers headersAssert = response.getHeaders();
        //System.out.println(headers); //x-secret-homework-header
        assertTrue(headersAssert.hasHeaderWithName("x-secret-homework-header"), "Response does not have header 'x-secret-homework-header'");
    }
}
