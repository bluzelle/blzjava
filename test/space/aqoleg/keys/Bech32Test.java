package space.aqoleg.keys;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Bech32Test {

    @Test
    void test() {
        check("a12uel5l", "a", "");
        check(
                "an83characterlonghumanreadablepartthatcontainsthenumber1andtheexcludedcharactersbio1tt5tgs",
                "an83characterlonghumanreadablepartthatcontainsthenumber1andtheexcludedcharactersbio",
                ""
        );
        check(
                "abcdef1qpzry9x8gf2tvdw0s3jn54khce6mua7lmqqqxw",
                "abcdef",
                "00443214c74254b635cf84653a56d7c675be77df"
        );
        check(
                "split1checkupstagehandshakeupstreamerranterredcaperred2y9e3w",
                "split",
                "c5f38b70305f519bf66d85fb6cf03058f3dde463ecd7918f2dc743918f2d"
        );
        assertEquals(
                "11qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqc8247j",
                Bech32.encode("1", new byte[51])
        );
        assertEquals(
                "11qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq" +
                        "qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq978ear",
                Bech32.encode("1", new byte[102])
        );
    }

    private static void check(String correctString, String prefix, String data) {
        byte[] bytes = new byte[data.length() / 2];
        for (int i = 0; i < data.length(); i += 2) {
            int firstChar = Character.digit(data.charAt(i), 16);
            int secondChar = Character.digit(data.charAt(i + 1), 16);
            bytes[i / 2] = (byte) (firstChar << 4 | secondChar);
        }
        assertEquals(correctString, Bech32.encode(prefix, bytes));
    }
}