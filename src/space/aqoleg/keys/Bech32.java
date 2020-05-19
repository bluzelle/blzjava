// bech32 encoder, see bip173
// usage:
//    String encoded = Bech32.encode(prefixString, dataBytes);
package space.aqoleg.keys;

import java.io.ByteArrayOutputStream;

public class Bech32 {
    private static final String alphabet = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";
    private static final int[] generator = {0x3b6a57b2, 0x26508e6d, 0x1ea119fa, 0x3d4233dd, 0x2a1462b3};

    /**
     * @param prefix String prefix
     * @param data   byte array with data
     * @return encoded prefix and data
     * @throws NullPointerException if prefix == null, or data == null
     */
    public static String encode(String prefix, byte[] data) {
        // 5-bits bytes
        data = convertToWords(data);

        // add one after prefix
        StringBuilder result = new StringBuilder(prefix);
        result.append("1");

        // calculate checksum of prefix
        int checksum = 1;
        for (int i = 0; i < prefix.length(); i++) {
            checksum = polymod(checksum, (byte) (prefix.charAt(i) >> 5));
        }
        checksum = polymod(checksum, (byte) 0);
        for (int i = 0; i < prefix.length(); i++) {
            checksum = polymod(checksum, (byte) (prefix.charAt(i) & 0b11111));
        }

        // calculate checksum of data and add encoded data to result
        for (byte b : data) {
            checksum = polymod(checksum, b);
            result.append(alphabet.charAt(b));
        }

        // calculate checksum of 6 empty bytes
        for (int i = 0; i < 6; i++) {
            checksum = polymod(checksum, (byte) 0);
        }
        checksum ^= 1;

        // add checksum to result
        for (int i = 25; i >= 0; i -= 5) {
            int word = (checksum >> i) & 0b11111;
            result.append(alphabet.charAt(word));
        }

        return result.toString();
    }

    // convert 8-bit bytes to 5-bit bytes
    private static byte[] convertToWords(byte[] bytes) {
        int buffer = 0;
        int bitsInBuffer = 0;
        ByteArrayOutputStream result = new ByteArrayOutputStream();

        for (byte b : bytes) {
            buffer = (buffer << 8) | (b & 0xFF);
            bitsInBuffer += 8;

            while (bitsInBuffer >= 5) {
                bitsInBuffer -= 5;
                result.write((byte) ((buffer >>> bitsInBuffer) & 0b11111));
            }
        }

        if (bitsInBuffer > 0) {
            result.write((byte) ((buffer << (5 - bitsInBuffer)) & 0b11111));
        }

        return result.toByteArray();
    }

    // checksum = polymod(checksum, aByte) for each aByte in byte[]
    private static int polymod(int checksum, byte value) {
        int b = checksum >> 25;

        checksum = ((checksum & 0x1ffffff) << 5);
        for (int i = 0; i < 5; i++) {
            if (((b >> i) & 1) == 1) {
                checksum ^= generator[i];
            }
        }
        return checksum ^ value;
    }
}