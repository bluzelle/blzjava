package com.bluzelle.keys;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HdKeyPairTest {

    @Test
    void test() {
        assertThrows(
                NullPointerException.class,
                () -> HdKeyPair.createMaster(new byte[]{2, 2}).generateChild(null)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> HdKeyPair.createMaster(new byte[]{2, 2}).generateChild("/-90")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> HdKeyPair.createMaster(new byte[]{2, 2}).generateChild("90/d")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> HdKeyPair.createMaster(new byte[]{2, 2}).generateChild("g6")
        );

        byte[] input = new BigInteger("000102030405060708090a0b0c0d0e0f", 16).toByteArray();
        byte[] seed = new byte[16];
        System.arraycopy(input, 0, seed, 16 - input.length, input.length);
        HdKeyPair master = HdKeyPair.createMaster(seed);
        isEqual(master, "0339a36013301597daef41fbe593a02cc513d0b55527ec2df1050e2e8ff49c85c2");
        isEqual(
                master.generateChild("0h/1/2h"),
                "0357bfe1e341d01c69fe5654309956cbea516822fba8a601743a012a7896ee8dc2"
        );
        isEqual(
                master.generateChild("/0h/1").generateChild("2h/2/1000000000/"),
                "022a471424da5e657499d1ff51cb43c47481a03b1e77f951fe64cec9f5a48f7011"
        );

        input = new BigInteger("fffcf9f6f3f0edeae7e4e1dedbd8d5d2cfccc9c6c3c0bdbab7b4b1aeaba8a5a29f9c999693"
                + "908d8a8784817e7b7875726f6c696663605d5a5754514e4b484542", 16).toByteArray();
        seed = new byte[64];
        System.arraycopy(input, 1, seed, 64 - (input.length - 1), input.length - 1);
        master = HdKeyPair.createMaster(seed);
        isEqual(
                master.generateChild("/0/2147483647h/"),
                "03c01e7425647bdefa82b12d9bad5e3e6865bee0502694b94ca58b666abc0a5c3b"
        );

        isEqual(
                master.generateChild("/0/2147483647h/1/2147483646h"),
                "02d2b36900396c9282fa14628566582f206a5dd0bcc8d5e892611806cafb0301f0"
        );

        isEqual(
                master.generateChild("/").generateChild("0/2147483647h/1/").generateChild("/2147483646h/2"),
                "024d902e1a2fc7a8755ab5b694c575fce742c48d9ff192e63df5193e4c7afe1f9c"
        );

        input = new BigInteger("4b381541583be4423346c643850da4b320e46a87ae3d2a4e6da11eba819cd4acba45d23931"
                + "9ac14f863b8d5ab5a0d0c64d2e8a1e7d1457df2e5a3c51c73235be", 16).toByteArray();
        seed = new byte[64];
        System.arraycopy(input, 0, seed, 64 - input.length, input.length);
        master = HdKeyPair.createMaster(seed);
        isEqual(
                master.generateChild("/0h"),
                "026557fdda1d5d43d79611f784780471f086d58e8126b8c40acb82272a7712e7f2"
        );

        master = HdKeyPair.createMaster(Mnemonic.createSeed("size tragic sausage", "mnemonic12"));
        isEqual(master, "039f508b6c628507b7b6880d3c699d7d46707116cc37a7a23fd16d8c1615660370");
        isEqual(
                master.generateChild("/42"),
                "0361a0530b31a6d3513eabd5563afc211f737edecbb85b798b74302facb63dc8c8"
        );
        isEqual(
                master.generateChild("/426788'"),
                "03edf3cef92c15528236e59e1b8e7efcfac11a245ff36863e8fa4a5408b09ef8d6"
        );
        isEqual(
                master.generateChild("/426788'/3"),
                "026a93829d643121afb62a8d7a7d9f864f5842611cefd93e4895d584e1603f710d"
        );

        master = HdKeyPair.createMaster(Mnemonic.createSeed("fury great shell", "mnemonic"));
        isEqual(
                master.generateChild("/426788'/677/5777'/9/0/0/8887h/78/0/7'"),
                "024ea2f83fe047d84ee3495b1f09041b78f05df849bd8fb5de1002e83cf5a7bd8c"
        );
        isEqual(
                master.generateChild("/426788'/677/5777'/9/0/0/8887'/78/0/17'"),
                "03ff17d1bf54f6321c52b45ad51522dadfb7e54d7becfb749c8a40c33b4a610dc4"
        );
    }

    private void isEqual(HdKeyPair hdKeyPair, String publicKey) {
        byte[] publicKeyBytes = hdKeyPair.publicKeyToByteArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : publicKeyBytes) {
            stringBuilder.append(String.format("%02x", b));
        }
        assertEquals(publicKey, stringBuilder.toString());
    }
}