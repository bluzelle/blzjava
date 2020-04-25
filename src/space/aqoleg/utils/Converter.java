// bytes-to-hex, hex-to-bytes and hex-to-string converters
package space.aqoleg.utils;

import space.aqoleg.exception.UtilException;

public class Converter {

    /**
     * @param bytes     array to be converted, big endian
     * @param prefix    if true, starts with "0x"
     * @param uppercase if true, return uppercase hex
     * @return String containing hex value of bytes
     * @throws NullPointerException if bytes == null
     */
    public static String bytesToHex(byte[] bytes, boolean prefix, boolean uppercase) {
        if (bytes.length == 0) {
            return "";
        }
        StringBuilder stringBuilder = prefix ? new StringBuilder("0x") : new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format(uppercase ? "%02X" : "%02x", b)); // zero-padded, min width 2
        }
        return stringBuilder.toString();
    }

    /**
     * @param hex String in hex, can start with 0x
     * @return byte array created from this hex String, big endian
     * @throws NullPointerException if hex == null
     * @throws UtilException        if string contains not accepted symbols
     */
    public static byte[] hexToBytes(String hex) {
        if (hex.startsWith("0x")) {
            hex = hex.substring(2);
        }
        if (hex.length() % 2 != 0) {
            hex = "0".concat(hex);
        }
        int length = hex.length();
        byte[] bytes = new byte[length / 2];
        int firstChar;
        int secondChar;
        for (int i = 0; i < length; i += 2) {
            firstChar = Character.digit(hex.charAt(i), 16);
            if (firstChar < 0) {
                throw new UtilException("unaccepted symbol " + hex.charAt(i));
            }
            secondChar = Character.digit(hex.charAt(i + 1), 16);
            if (secondChar < 0) {
                throw new UtilException("unaccepted symbol " + hex.charAt(i + 1));
            }
            bytes[i / 2] = (byte) (firstChar << 4 | secondChar);
        }
        return bytes;
    }

    /**
     * @param hex String in hex, can start with 0x
     * @return String created from this hex String
     * @throws NullPointerException if hex == null
     * @throws UtilException        if string contains not accepted symbols
     */
    public static String hexToString(String hex) {
        if (hex.startsWith("0x")) {
            hex = hex.substring(2);
        }
        if (hex.length() % 2 != 0) {
            hex = "0".concat(hex);
        }

        StringBuilder builder = new StringBuilder();
        int firstChar;
        int secondChar;
        int length = hex.length();
        for (int i = 0; i < length; i += 2) {
            firstChar = Character.digit(hex.charAt(i), 16);
            if (firstChar < 0) {
                throw new UtilException("unaccepted symbol " + hex.charAt(i));
            }
            secondChar = Character.digit(hex.charAt(i + 1), 16);
            if (secondChar < 0) {
                throw new UtilException("unaccepted symbol " + hex.charAt(i + 1));
            }
            builder.append((char) (firstChar << 4 | secondChar));
        }
        return builder.toString();
    }
}