// some utils
// usage:
//    String bluzelleAddress = Utils.getAddress(hdKeyPair);
//    byte[] sha256hash = Utils.sha256hash(messageBytes);
//    String memo = Utils.randomString();
//    String base64 = Utils.base64encode(messageBytes);
//    String urlEncoded = Utils.urlEncode(string);
//    String encoded = Utils.hexToString(hexString);
package space.aqoleg.bluzelle;

import space.aqoleg.keys.Bech32;
import space.aqoleg.keys.HdKeyPair;
import space.aqoleg.keys.Ripemd160;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SuppressWarnings("WeakerAccess")
public class Utils {
    private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private static final String hex = "0123456789ABCDEF";
    private static final MessageDigest sha256;

    static {
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * @param keyPair HdKeyPair keypair from which will be created address
     * @return String address
     * @throws NullPointerException if keyPair == null
     */
    public static String getAddress(HdKeyPair keyPair) {
        byte[] hash = Ripemd160.getHash(sha256hash(keyPair.publicKeyToByteArray()));
        return Bech32.encode("bluzelle", hash);
    }

    /**
     * @param message array containing message to be hashed with sha256
     * @return 32-bytes hash
     * @throws NullPointerException if message == null
     */
    public static byte[] sha256hash(byte[] message) {
        return sha256.digest(message);
    }

    /**
     * @return random string with length 32 contains uppercase and lowercase letters and numbers
     */
    public static String randomString() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            out.append(alphabet.charAt((int) (Math.random() * 62)));
        }
        return out.toString();
    }

    /**
     * @param bytes array containing bytes to be encoded
     * @return bytes encoded in base64 String
     * @throws NullPointerException if bytes == null
     */
    public static String base64encode(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        int bytesPos = 0;
        int bytesLength = bytes.length;
        while (bytesPos < bytesLength) {
            // in  0 1 2 3 4 5 6 7|0 1 2 3 4 5 6 7|0 1 2 3 4 5 6 7
            // out 0 1 2 3 4 5|6 7 0 1 2 3|4 5 6 7 0 1|2 3 4 5 6 7
            int in0 = bytes[bytesPos++] & 0xff;
            int in1 = bytesPos < bytesLength ? bytes[bytesPos++] & 0xff : -1;
            int in2 = bytesPos < bytesLength ? bytes[bytesPos++] & 0xff : -1;

            int out = in0 >>> 2;
            builder.append(alphabet.charAt(out));

            out = (in0 & 0b11) << 4;
            if (in1 < 0) {
                builder.append(alphabet.charAt(out));
                builder.append("==");
                break;
            }
            out |= in1 >>> 4;
            builder.append(alphabet.charAt(out));

            out = (in1 & 0b1111) << 2;
            if (in2 < 0) {
                builder.append(alphabet.charAt(out));
                builder.append("=");
                break;
            }
            out |= in2 >>> 6;
            builder.append(alphabet.charAt(out));

            out = in2 & 0b111111;
            builder.append(alphabet.charAt(out));
        }
        return builder.toString();
    }

    /**
     * @param string String to be encoded
     * @return url encoded string
     * @throws NullPointerException if string == null
     */
    public static String urlEncode(String string) {
        StringBuilder builder = new StringBuilder();

        int pos = 0;
        int length = string.length();
        while (pos < length) {
            char c = string.charAt(pos++);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                builder.append(c);
                continue;
            }
            switch (c) {
                case ';':
                case ',':
                case '/':
                case ':':
                case '@':
                case '&':
                case '=':
                case '+':
                case '$':
                case '-':
                case '_':
                case '.':
                case '!':
                case '~':
                case '*':
                case '\'':
                case '(':
                case ')':
                    builder.append(c);
                    continue;
            }
            if (c <= 0x007F) {
                // 0 1 1 1 1 1 1 1
                // 0 x x x|x x x x
                builder.append("%");
                builder.append(hex.charAt(c >> 4));
                builder.append(hex.charAt(c & 0b1111));
            } else if (c <= 0x07FF) {
                // 0 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1
                //     1 1 0 x|x x x x
                builder.append("%");
                builder.append(hex.charAt(0b1100 | c >> 10));
                builder.append(hex.charAt((c >> 6) & 0b1111));
                // 0 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1
                //                 1 0 x x|x x x x
                builder.append("%");
                builder.append(hex.charAt(0b1000 | ((c >> 4) & 0b0011)));
                builder.append(hex.charAt(c & 0b1111));
            } else {
                // 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
                // 1 1 1 0|x x x x
                builder.append("%E");
                builder.append(hex.charAt((c & 0xFFFF) >> 12));
                // 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
                //             1 0 x x|x x x x
                builder.append("%");
                builder.append(hex.charAt(0b1000 | ((c >> 10) & 0b0011)));
                builder.append(hex.charAt((c >> 6) & 0b1111));
                // 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
                //                         1 0 x x|x x x x
                builder.append("%");
                builder.append(hex.charAt(0b1000 | ((c >> 4) & 0b0011)));
                builder.append(hex.charAt(c & 0b1111));
            }
        }
        return builder.toString();
    }

    /**
     * @param hex String in hex, can start with 0x
     * @return String created from this hex String
     * @throws NullPointerException     if hex == null
     * @throws IllegalArgumentException if hex contain non-hex symbols
     */
    public static String hexToString(String hex) {
        int length = hex.length();
        int pos = 0;
        if (length > 1 && hex.charAt(1) == 'x') {
            pos = 2;
        }

        StringBuilder builder = new StringBuilder();
        if (length % 2 != 0) {
            builder.append((char) toInt(hex.charAt(pos++)));
        }
        while (pos < length) {
            int c = toInt(hex.charAt(pos++)) << 4 | toInt(hex.charAt(pos++));
            if ((c & 0b10000000) != 0) {
                if ((c & 0b00100000) == 0) {
                    // 1 1 0 x|x x x x
                    //             1 0 x x|x x x x
                    // 0 0 0 1 1 1 1 1 1 1 1 1 1 1
                    c = (c & 0b00011111) << 6;
                    c |= (toInt(hex.charAt(pos++)) & 0b11) << 4;
                    c |= toInt(hex.charAt(pos++));
                } else {
                    // 1 1 1 0|x x x x
                    //             1 0 x x|x x x x
                    //                         1 0 x x|x x x x
                    // 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
                    c = (c & 0b1111) << 12;
                    c |= (toInt(hex.charAt(pos++)) & 0b11) << 10;
                    c |= toInt(hex.charAt(pos++)) << 6;
                    c |= (toInt(hex.charAt(pos++)) & 0b11) << 4;
                    c |= toInt(hex.charAt(pos++));
                }
            }
            builder.append((char) c);
        }
        return builder.toString();
    }

    private static int toInt(char hex) {
        if (hex >= 'a' && hex <= 'f') {
            return hex - 87;
        }
        if (hex >= 'A' && hex <= 'F') {
            return hex - 55;
        }
        if (hex >= '0' && hex <= '9') {
            return hex - 48;
        }
        throw new IllegalArgumentException("non-hex symbol " + hex);
    }
}