package exercises;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CookieEx11 {
    @Test
    public void testCookie(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        Map<String,String> responseCookies = response.getCookies();
        String cookiesAssert = responseCookies.toString();
        //System.out.println(cookiesAssert); // {HomeWork=hw_value}

        assertEquals("{HomeWork=hw_value}", cookiesAssert, "!!!Wrong Cookies!!!");
    }

}