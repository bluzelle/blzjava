package com.bluzelle.keys;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class EccTest {

    @Test
    void gMultiply() {
        Ecc ecc = Ecc.ecc;
        assertThrows(NullPointerException.class, () -> ecc.gMultiply(null));

        areTheSamePoint(
                ecc.gMultiply(BigInteger.valueOf(1)),
                "79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798",
                "483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8"
        );
        areTheSamePoint(
                ecc.gMultiply(BigInteger.valueOf(2)),
                "C6047F9441ED7D6D3045406E95C07CD85C778E4B8CEF3CA7ABAC09B95C709EE5",
                "1AE168FEA63DC339A3C58419466CEAEEF7F632653266D0E1236431A950CFE52A"
        );
        areTheSamePoint(
                ecc.gMultiply(BigInteger.valueOf(3)),
                "F9308A019258C31049344F85F89D5229B531C845836F99B08601F113BCE036F9",
                "388F7B0F632DE8140FE337E62A37F3566500A99934C2231B6CB9FD7584B8E672"
        );
        areTheSamePoint(
                ecc.gMultiply(BigInteger.valueOf(4)),
                "E493DBF1C10D80F3581E4904930B1404CC6C13900EE0758474FA94ABE8C4CD13",
                "51ED993EA0D455B75642E2098EA51448D967AE33BFBDFE40CFE97BDC47739922"
        );
        areTheSamePoint(
                ecc.gMultiply(BigInteger.valueOf(5)),
                "2F8BDE4D1A07209355B4A7250A5C5128E88B84BDDC619AB7CBA8D569B240EFE4",
                "D8AC222636E5E3D6D4DBA9DDA6C9C426F788271BAB0D6840DCA87D3AA6AC62D6"
        );
        areTheSamePoint(
                ecc.gMultiply(ecc.n.pow(2).add(BigInteger.valueOf(5))),
                "2F8BDE4D1A07209355B4A7250A5C5128E88B84BDDC619AB7CBA8D569B240EFE4",
                "D8AC222636E5E3D6D4DBA9DDA6C9C426F788271BAB0D6840DCA87D3AA6AC62D6"
        );
        areTheSamePoint(
                ecc.gMultiply(BigInteger.valueOf(6)),
                "FFF97BD5755EEEA420453A14355235D382F6472F8568A18B2F057A1460297556",
                "AE12777AACFBB620F3BE96017F45C560DE80F0F6518FE4A03C870C36B075F297"
        );
        areTheSamePoint(
                ecc.gMultiply(BigInteger.valueOf(7)),
                "5CBDF0646E5DB4EAA398F365F2EA7A0E3D419B7E0330E39CE92BDDEDCAC4F9BC",
                "6AEBCA40BA255960A3178D6D861A54DBA813D0B813FDE7B5A5082628087264DA"
        );
        areTheSamePoint(
                ecc.gMultiply(ecc.n.negate().add(BigInteger.valueOf(7))),
                "5CBDF0646E5DB4EAA398F365F2EA7A0E3D419B7E0330E39CE92BDDEDCAC4F9BC",
                "6AEBCA40BA255960A3178D6D861A54DBA813D0B813FDE7B5A5082628087264DA"
        );
        areTheSamePoint(
                ecc.gMultiply(ecc.n.pow(7).negate().add(BigInteger.valueOf(8))),
                "2F01E5E15CCA351DAFF3843FB70F3C2F0A1BDD05E5AF888A67784EF3E10A2A01",
                "5C4DA8A741539949293D082A132D13B4C2E213D6BA5B7617B5DA2CB76CBDE904"
        );
        areTheSamePoint(
                ecc.gMultiply(BigInteger.valueOf(16)),
                "E60FCE93B59E9EC53011AABC21C23E97B2A31369B87A5AE9C44EE89E2A6DEC0A",
                "F7E3507399E595929DB99F34F57937101296891E44D23F0BE1F32CCE69616821"
        );
        areTheSamePoint(
                ecc.gMultiply(BigInteger.valueOf(20)),
                "4CE119C96E2FA357200B559B2F7DD5A5F02D5290AFF74B03F3E471B273211C97",
                "12BA26DCB10EC1625DA61FA10A844C676162948271D96967450288EE9233DC3A"
        );
        areTheSamePoint(
                ecc.gMultiply(BigInteger.valueOf(21)),
                "352BBF4A4CDD12564F93FA332CE333301D9AD40271F8107181340AEF25BE59D5",
                "321EB4075348F534D59C18259DDA3E1F4A1B3B2E71B1039C67BD3D8BCF81998C"
        );
        areTheSamePoint(
                ecc.gMultiply(new BigInteger("AA5E28D6A97A2479A65527F7290311A3624D4CC0FA1578598EE3C2613BF99522", 16)),
                "34F9460F0E4F08393D192B3C5133A6BA099AA0AD9FD54EBCCFACDFA239FF49C6",
                "B71EA9BD730FD8923F6D25A7A91E7DD7728A960686CB5A901BB419E0F2CA232"
        );
        areTheSamePoint(
                ecc.gMultiply(new BigInteger("7E2B897B8CEBC6361663AD410835639826D590F393D90A9538881735256DFAE3", 16)),
                "D74BF844B0862475103D96A611CF2D898447E288D34B360BC885CB8CE7C00575",
                "131C670D414C4546B88AC3FF664611B1C38CEB1C21D76369D7A7A0969D61D97D"
        );
        areTheSamePoint(
                ecc.gMultiply(new BigInteger("376A3A2CDCD12581EFFF13EE4AD44C4044B8A0524C42422A7E1E181E4DEECCEC", 16)),
                "14890E61FCD4B0BD92E5B36C81372CA6FED471EF3AA60A3E415EE4FE987DABA1",
                "297B858D9F752AB42D3BCA67EE0EB6DCD1C2B7B0DBE23397E66ADC272263F982"
        );
        areTheSamePoint(
                ecc.gMultiply(new BigInteger("1B22644A7BE026548810C378D0B2994EEFA6D2B9881803CB02CEFF865287D1B9", 16)),
                "F73C65EAD01C5126F28F442D087689BFA08E12763E0CEC1D35B01751FD735ED3",
                "F449A8376906482A84ED01479BD18882B919C140D638307F0C0934BA12590BDE"
        );
        areTheSamePoint(
                ecc.gMultiply(new BigInteger("ebb2c082fd7727890a28ac82f6bdf97bad8de9f5d7c9028692de1a255cad3e0f", 16)),
                "779DD197A5DF977ED2CF6CB31D82D43328B790DC6B3B7D4437A427BD5847DFCD",
                "E94B724A555B6D017BB7607C3E3281DAF5B1699D6EF4124975C9237B917D426F"
        );
    }

    @Test
    void signVerify() {
        assertThrows(NullPointerException.class, () -> Ecc.ecc.sign(null, BigInteger.ONE));
        assertThrows(NullPointerException.class, () -> Ecc.ecc.sign(new byte[]{23, 3}, null));
        byte[] msg = new byte[33];
        msg[0] = 1;
        assertThrows(IllegalArgumentException.class, () -> Ecc.ecc.sign(msg, BigInteger.ONE));

        Ecc.Point point = Ecc.ecc.gMultiply(BigInteger.TEN);
        byte[] s = new byte[64];
        s[9] = 90;
        s[45] = 90;
        assertThrows(NullPointerException.class, () -> Ecc.ecc.verify(null, point, s));
        assertThrows(NullPointerException.class, () -> Ecc.ecc.verify(new byte[]{23, 3}, null, s));
        assertThrows(NullPointerException.class, () -> Ecc.ecc.verify(new byte[]{23, 3}, point, null));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> Ecc.ecc.verify(new byte[]{3, 3}, point, new byte[13]));

        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            BigInteger privateKey = new BigInteger(31, random);
            Ecc.Point publicKey = Ecc.ecc.gMultiply(privateKey);
            byte[] message = new BigInteger(31, random).toByteArray();
            byte[] signature = Ecc.ecc.sign(message, privateKey);
            message[1]++;
            assertFalse(Ecc.ecc.verify(message, publicKey, signature));
            message[1]--;
            signature[10]++;
            assertFalse(Ecc.ecc.verify(message, publicKey, signature));
            signature[10]--;
            assertTrue(Ecc.ecc.verify(message, publicKey, signature));
        }

        BigInteger privateKey = new BigInteger(31, random);
        Ecc.Point publicKey = Ecc.ecc.gMultiply(privateKey);
        byte[] message = new BigInteger(31, random).toByteArray();
        byte[] signature = Ecc.ecc.sign(message, privateKey);
        assertFalse(Ecc.ecc.verify(new byte[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, publicKey, signature));
        assertFalse(Ecc.ecc.verify(message, Ecc.ecc.gMultiply(BigInteger.TEN), signature));
        signature[9]++;
        assertFalse(Ecc.ecc.verify(message, publicKey, signature));
        signature[9]--;
        signature[37]--;
        assertFalse(Ecc.ecc.verify(message, publicKey, signature));
        signature[37]++;
        assertTrue(Ecc.ecc.verify(message, publicKey, signature));
    }

    private static void areTheSamePoint(Ecc.Point point, String x, String y) {
        assertEquals(x, point.x.toString(16).toUpperCase());
        assertEquals(y, point.y.toString(16).toUpperCase());
    }
}