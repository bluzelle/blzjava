package space.aqoleg.bluzelle.test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static space.aqoleg.bluzelle.Utils.randomString;
import static space.aqoleg.bluzelle.Utils.urlEncode;

class UtilTest {

    @Test
    void randomStringTest() {
        String string = randomString();
        assertEquals(32, string.length());
        System.out.println(string);
        System.out.println(randomString());
    }

    @Test
    void urlEncodeTest() {
        String string = urlEncode("{}><#?%\"[]^`abcfzAhvYZ0129  ?å~");
        assertEquals("%7B%7D%3E%3C%23%3F%25%22%5B%5D%5E%60abcfzAhvYZ0129%20%20%3F%C3%A5~", string);
    }
}