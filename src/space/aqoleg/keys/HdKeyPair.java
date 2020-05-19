// hierarchical deterministic keys created from the seed, see bip32
// usage:
//    HdKeyPair hdKeyPair = HdKeyPair.createMaster(seedBytes);
//    HdKeyPair hdKeyPair = hdKeyPair.generateChild(pathString);
//    BigInteger privateKey = hdKeyPair.d;
//    byte[] publicKey = hdKeyPair.publicKeyToByteArray();
//
// public key bytes:
//    1 byte, even - 0x02, odd - 0x03
//    byte[32], x
package space.aqoleg.keys;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class HdKeyPair {
    public final BigInteger d; // private key as a number
    private final Ecc.Point point;
    private final byte[] chainCode; // 32-bytes chain code

    private HdKeyPair(BigInteger privateKey, byte[] chainCode) {
        if (privateKey.compareTo(Ecc.ecc.n) >= 0) {
            throw new UnsupportedOperationException("private key is too big, requires d < n");
        }
        d = privateKey;
        point = Ecc.ecc.gMultiply(d);
        this.chainCode = chainCode;
    }

    /**
     * @param seed array containing seed
     * @return master HdKeyPair created from this seed
     * @throws UnsupportedOperationException if created private key is not valid
     */
    public static HdKeyPair createMaster(byte[] seed) {
        // data = hmac-sha512(key = "Bitcoin seed", data = seed);
        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec("Bitcoin seed".getBytes(), "HmacSHA512"));
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        byte[] data = mac.doFinal(seed);

        // the first 32 bytes is the private key, the last 32 bytes is the chain code
        BigInteger privateKey = new BigInteger(1, Arrays.copyOfRange(data, 0, 32));
        byte[] chainCode = Arrays.copyOfRange(data, 32, 64);
        return new HdKeyPair(privateKey, chainCode);
    }

    /**
     * @return 33-bytes array containing this public key in compressed form
     */
    public byte[] publicKeyToByteArray() {
        byte[] bytes = new byte[33];
        bytes[0] = (byte) (point.y.testBit(0) ? 0x03 : 0x02);
        byte[] x = point.x.toByteArray();
        // BigInteger byte array is the signed two's-complement representation, so the first byte can be 0
        int xStart = x[0] == 0 ? 1 : 0;
        System.arraycopy(x, xStart, bytes, 33 - (x.length - xStart), x.length - xStart);
        return bytes;
    }

    /**
     * @param path String containing relative path of this child as /11/3' or 11/0h/12'/
     * @return child HdKeyPair created from this HdKeyPair
     * @throws NullPointerException          if path == null
     * @throws IllegalArgumentException      if path is incorrect
     * @throws UnsupportedOperationException if created private key is not valid
     */
    public HdKeyPair generateChild(String path) {
        try {
            HdKeyPair keyPair = this;
            int pos = path.charAt(0) == '/' ? 1 : 0;
            boolean theLast = false;
            do {
                int stopPos = path.indexOf('/', pos);
                if (stopPos == -1) {
                    stopPos = path.length();
                    theLast = true;
                }
                char lastChar = path.charAt(stopPos - 1);
                if (lastChar == '/') {
                    break;
                }
                boolean hardened = lastChar == '\'' || lastChar == 'h';
                int keyNumber = Integer.parseInt(path.substring(pos, hardened ? stopPos - 1 : stopPos));
                keyPair = keyPair.generateChild(keyNumber, hardened);
                pos = stopPos + 1;
            } while (!theLast);
            return keyPair;
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("path is incorrect " + e.getMessage());
        }
    }

    private HdKeyPair generateChild(int keyNumber, boolean isHardened) {
        if (keyNumber < 0) {
            throw new IllegalArgumentException("negative keyNumber");
        }
        byte[] data = new byte[37];
        // byte[33], key, for hardened first byte is zero, for non-hardened use compressed public key
        // intBE, key number, += 2^31 for the hardened keys
        if (isHardened) {
            byte[] key = d.toByteArray();
            // BigInteger byte array is the signed two's-complement representation, so the first byte can be 0
            int keyStart = key[0] == 0 ? 1 : 0;
            System.arraycopy(key, keyStart, data, 33 - (key.length - keyStart), key.length - keyStart);
        } else {
            byte[] key = publicKeyToByteArray();
            System.arraycopy(key, 0, data, 0, 33);
        }
        data[33] = (byte) (keyNumber >>> 24);
        data[34] = (byte) (keyNumber >>> 16);
        data[35] = (byte) (keyNumber >>> 8);
        data[36] = (byte) keyNumber;
        if (isHardened) {
            data[33] |= 0b10000000;
        }

        // data = hmac-sha512(key = parent chain code, data);
        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(chainCode, "HmacSHA512"));
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        data = mac.doFinal(data);

        // child chain code is the last 32 bytes of the data
        byte[] chainCode = Arrays.copyOfRange(data, 32, 64);
        // child private key = (the first 32 bytes of the data + parent private key) % n
        BigInteger privateKey = new BigInteger(1, Arrays.copyOfRange(data, 0, 32));
        if (privateKey.compareTo(Ecc.ecc.n) >= 0) {
            throw new UnsupportedOperationException("private key is not less than n");
        }
        privateKey = d.add(privateKey).mod(Ecc.ecc.n);
        if (privateKey.signum() <= 0) {
            throw new UnsupportedOperationException("private key is zero");
        }
        return new HdKeyPair(privateKey, chainCode);
    }
}