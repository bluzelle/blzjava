// makes seed from the mnemonic, see bip39
// usage:
//    byte[] seed = Mnemonic.createSeed(mnemonicString, passphraseString);
package com.bluzelle.keys;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Mnemonic {
    private static final Mac mac;

    static {
        try {
            mac = Mac.getInstance("HmacSHA512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * @param mnemonic   words in utf-8
     * @param passphrase in utf-8, can be empty string
     * @return 64 bytes seed
     * @throws NullPointerException if mnemonic == null, or passphrase == null
     */
    public static byte[] createSeed(String mnemonic, String passphrase) {
        // seed = pbkdf2(prf, password, salt, c, dkLen), password-based key derivation function 2
        // the pseudo-random function prf = hmac-sha512, password = mnemonic, salt = passphrase
        // the number of the iterations c = 2048, the desired bit length of the key dkLen = 512

        // pbkdf2 = t1 || t2 || ... || tdkLen/hLen, hLen = prf.length, dkLen/hLen = 512 / 512 = 1
        // ti = u1 xor u2 xor ... xor uc
        // u1 = prf(password, salt || (4-byte big-endian i))
        // un = prf(password, un-1)

        // seed = u1 xor u2 xor ... xor u2048
        // u1 = prf(password, salt || 0x00000001)
        // un = prf(password, un-1)

        try {
            mac.init(new SecretKeySpec(mnemonic.getBytes(), "HmacSHA512"));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // key = salt || 0x00000001
        byte[] passphraseBytes = passphrase.getBytes();
        byte[] salt = new byte[passphraseBytes.length + 4];
        System.arraycopy(passphraseBytes, 0, salt, 0, passphraseBytes.length);
        salt[salt.length - 1] = 0x01;
        // seed = u = u1
        byte[] u = mac.doFinal(salt);
        byte[] seed = u;
        // u = u2...u2048, seed ^= u
        for (int i = 1; i < 2048; i++) {
            u = mac.doFinal(u);
            for (int j = 0; j < 64; j++) {
                seed[j] ^= u[j];
            }
        }
        return seed;
    }
}