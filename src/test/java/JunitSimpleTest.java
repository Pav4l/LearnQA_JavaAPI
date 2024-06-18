import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JunitSimpleTest {

    @Test
    public void testFor200(){

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/map")
                .andReturn();
        //assertTrue(response.statusCode() == 200, "Unexpected status code");
        assertEquals(200, response.statusCode(), "Unexpected status code");
    }

    @Test
    public void testFor404(){

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/map2")
                .andReturn();
        //assertTrue(response.statusCode() == 200, "Unexpected status code");
        assertEquals(404, response.statusCode(), "Unexpected status code");
    }
}
