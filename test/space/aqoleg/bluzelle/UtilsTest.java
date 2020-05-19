package space.aqoleg.bluzelle;

import org.junit.jupiter.api.Test;
import space.aqoleg.keys.HdKeyPair;
import space.aqoleg.keys.Mnemonic;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static space.aqoleg.bluzelle.Utils.*;

class UtilsTest {

    @Test
    void getAddressTest() {
        String mnemonic = "around buzz diagram captain obtain detail salon mango muffin brother morning jeans" +
                " display attend knife carry green dwarf vendor hungry fan route pumpkin car";
        HdKeyPair master = HdKeyPair.createMaster(Mnemonic.createSeed(mnemonic, "mnemonic"));
        HdKeyPair keyPair = master.generateChild("44'/118'/0'/0/0");
        assertEquals("bluzelle1upsfjftremwgxz3gfy0wf3xgvwpymqx754ssu9", getAddress(keyPair));

        mnemonic = "volcano arrest ceiling physical concert sunset absent hungry tobacco canal census era pretty" +
                " car code crunch inside behind afraid express giraffe reflect stadium luxury";
        master = HdKeyPair.createMaster(Mnemonic.createSeed(mnemonic, "mnemonic"));
        keyPair = master.generateChild("44'/118'/0'/0/0");
        assertEquals("bluzelle1xhz23a58mku7ch3hx8f9hrx6he6gyujq57y3kp", getAddress(keyPair));
    }

    @Test
    void sha256hashTest() {
        areEquals(
                "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
                sha256hash("abc".getBytes())
        );

        byte[] millionA = new byte[1000000];
        Arrays.fill(millionA, (byte) 'a');
        areEquals(
                "cdc76e5c9914fb9281a1c7e284d73e67f1809a48a497200e046d39ccc7112cd0",
                sha256hash(millionA)
        );
    }

    @Test
    void randomStringTest() {
        String string = randomString();
        assertEquals(32, string.length());
        assertNotEquals(string, randomString());
        assertNotEquals(randomString(), randomString());
        System.out.println(string);
        System.out.println(randomString());
    }

    @Test
    void base64EncodeTest() {
        assertEquals("", base64encode("".getBytes()));
        assertEquals("Zg==", base64encode("f".getBytes()));
        assertEquals("Zm8=", base64encode("fo".getBytes()));
        assertEquals("Zm9v", base64encode("foo".getBytes()));
        assertEquals("Zm9vYg==", base64encode("foob".getBytes()));
        assertEquals("Zm9vYmE=", base64encode("fooba".getBytes()));
        assertEquals("Zm9vYmFy", base64encode("foobar".getBytes()));
        assertEquals("NyE/", base64encode("7!?".getBytes()));
    }

    @Test
    void urlEncodeTest() {
        assertEquals(
                "%7B%7D%3E%3C%23%3F%25%22%5B%5D%5E%60abcfzAhvYZ0129%20%20",
                urlEncode("{}><#?%\"[]^`abcfzAhvYZ0129  ")
        );
        assertEquals(
                ";,/:@&=+$-_.!~*\'()abzALZ019",
                urlEncode(";,/:@&=+$-_.!~*\'()abzALZ019")
        );
        assertEquals(
                "%20%22%23%25=%7B%7F%3F",
                urlEncode(" \"#%={\u007f?")
        );
        assertEquals(
                "%D5%95%CA%AA%DF%87%D4%BF%D1%B8%D1%85%D1%81%D0%BE%D0%BB%D0%BE%D0%BD%D0%B3",
                urlEncode("\u0555\u02aa\u07C7\u053F\u0478хсолонг")
        );
        assertEquals(
                "%EA%AA%AA%E5%95%95%E7%B5%9A%EF%91%9C",
                urlEncode("\uAAAA\u5555\u7d5a\uf45c")
        );
        assertEquals(
                "38-9%D0%B083*%3F*!%E2%84%96*%3Fever%D0%A0*%D0%81%60%D1%83%D0%BB%D1%8C%D0%BC%D1%83%D0%BA0384%D0%BE" +
                        "9%D0%A2%D0%A0%D0%93~%D1%8E%D1%8E.%20%20%20%5Cw%E2%8C%B3%E2%8C%B3%E6%9D%A6",
                urlEncode("38-9а83*?*!№*?everР*Ё`ульмук0384о9ТРГ~юю.   \\w\u2333\u2333\u6766")
        );
    }

    @Test
    void hexToStringTest() {
        assertThrows(NullPointerException.class, () -> hexToString(null));
        assertThrows(IllegalArgumentException.class, () -> hexToString("0xdq"));
        assertThrows(IllegalArgumentException.class, () -> hexToString("0xr4"));
        assertEquals("", hexToString(""));
        assertEquals("..DOOM..", hexToString("2E2E444F4F4D2E2E"));
        assertEquals("..DOOM..", hexToString("0x2E2E444F4F4D2E2E"));
        assertEquals("..DOOM..", hexToString("0x2e2e444f4f4d2E2E"));
        assertEquals("A.?,,,88jn jj", hexToString("412e3f2c2c2c38386a6e206a6a"));
        assertEquals("" + (char) 3 + (char) 4, hexToString("304"));
        assertEquals("" + (char) 3 + (char) 4, hexToString("0x304"));
        assertEquals(
                " \"#%{\u007f?",
                hexToString("0x202223257B7F3F")
        );
        assertEquals(
                "\u0555\u02aa\u07C7\u053F\u0478хсолонг",
                hexToString("D595CAAADF87D4BFD1B8D185D181D0BED0BBD0BED0BDD0B3")
        );
        assertEquals(
                "\uAAAA\u5555\u7d5a\uf45c",
                hexToString("EAAAAAE59595E7B59AEF919C")
        );
        assertEquals(
                "?№?РЁ`ульмукоТРГюю   \u2333\u2333",
                hexToString("3FE284963FD0A0D08160D183D0BBD18CD0BCD183D0BAD0BED0A2D0A0D093D18ED18E202020E28CB3E28CB3")
        );
    }

    private static void areEquals(String hex, byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format("%02x", b));
        }
        assertEquals(hex, stringBuilder.toString());
    }
}