package Exercises;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class LongRedirectEx7 {

    @Test
    public void testLongRedirect() {
        String url = "https://playground.learnqa.ru/api/long_redirect";
        int redirectCount = 0;
        Response response;

        do {
            response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .get(url)
                    .andReturn();

            String locationHeader = response.getHeader("Location");
            if (locationHeader != null) {
                url = locationHeader;
                redirectCount++;
                System.out.println("Redirect to: " + url);
            } else {
                break;
            }
        } while (response.getStatusCode() != 200);

        System.out.println("Final URL: " + url);
        System.out.println("Number of redirects: " + redirectCount);
    }
}
