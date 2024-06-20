package exercises;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ShortPhraseEx10 {

    @Test
    public void testStringLength() {
        String testString = "This is a test string";

        assertTrue(testString.length() > 15, "The string is shorter than 15 characters.");
    }
}