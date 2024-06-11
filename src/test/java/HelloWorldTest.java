import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class HelloWorldTest {

    @Test
    public void testResAssured(){

        Response response = RestAssured
                .given()
                .redirects()
                .follow(true)
                .post("https://playground.learnqa.ru/api/get_303")
                .andReturn();

        int statusCode = response.getStatusCode();
        System.out.println(statusCode);
        response.print();
    }
}
