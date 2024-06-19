package Exercises;

import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;

import java.util.ArrayList;
import java.util.Map;

public class ParsingJSONEx5 {
    @Test
    public void testRestAssured(){
        JsonPath response= RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

//      response.prettyPrint();

        ArrayList messages = response.get("messages");
        System.out.println(messages.get(1));

//        Map<String, String> message2 = response.get("messages[1]");
//        if (message2 == null){
//            System.out.println("The key 'message2' is absent");
//        } else {
//            System.out.println(message2);
//        }
    }
}