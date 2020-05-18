package space.aqoleg.bluzelle;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class BluezelleTest {
    private GasInfo gasInfo = new GasInfo(0, 0, 1000);
    private LeaseInfo leaseInfo = new LeaseInfo(0, 1, 0, 0);

    @Test
    void accounTest() {
        Bluzelle bluzelle = connect();
        System.out.println("version " + bluzelle.version());
        System.out.println("account " + bluzelle.account().toString());
    }

    @Test
    void crudTest() {
        Bluzelle bluzelle = connect();
        String key = "key";
        String value = "Ф  rr \u8900  ..";
        bluzelle.create(key, value, gasInfo, leaseInfo);
        assertTrue(bluzelle.has(key));
        assertTrue(bluzelle.txHas(key, gasInfo));
        assertEquals(value, bluzelle.read(key, true));
        bluzelle.delete(key, gasInfo);
    }

    @Test
    void test1() {
        Bluzelle bluzelle = connect();
        if (bluzelle.has("key Д\" =? k")) {
            bluzelle.delete("key Д\" =? k", gasInfo);
        }
        bluzelle.create("key Д\" =? k", "value немаловероятно", gasInfo, leaseInfo);
        assertEquals("value немаловероятно", bluzelle.read("key Д\" =? k", false));
        assertNull(bluzelle.read("nokey", true));
        assertEquals("value немаловероятно", bluzelle.txRead("key Д\" =? k", gasInfo));
        bluzelle.delete("key Д\" =? k", gasInfo);
    }

    @Test
    void test() {
        Bluzelle bluzelle = connect();
        bluzelle.deleteAll(gasInfo);
        bluzelle.create("newkeytest1", "value 1221 1 1 1", gasInfo, null);
        bluzelle.create("newkeytest2", "33", gasInfo, null);
        bluzelle.create("newkeytest3", "value", gasInfo, null);
        bluzelle.create("newkeytest4", "new value test 4", gasInfo, null);
        assertNull(bluzelle.read("nonexistingkey", true));
        System.out.println("version " + bluzelle.version());
        assertEquals("value 1221 1 1 1", bluzelle.read("newkeytest1", false));
        assertEquals("value", bluzelle.read("newkeytest3", true));
        bluzelle.rename("newkeytest4", "Mkey", gasInfo);
        assertEquals("new value test 4", bluzelle.txRead("Mkey", gasInfo));
        bluzelle.deleteAll(gasInfo);
        System.out.println("account " + bluzelle.account().toString());
        assertEquals(0, bluzelle.count());
        bluzelle.create("key1", "key1", gasInfo, leaseInfo);
        bluzelle.create("key2", "34 44 null", gasInfo, leaseInfo);
        bluzelle.create("key3", "value3", gasInfo, leaseInfo);
        bluzelle.create("key4", "new value test 4", gasInfo, leaseInfo);
        assertEquals(4, bluzelle.count());
        bluzelle.keyValues().forEach((key, value) -> System.out.println(key + ":" + value));
        assertTrue(bluzelle.has("key1"));
        bluzelle.rename("key1", "Mkey", gasInfo);
        assertFalse(bluzelle.has("key1"));
        bluzelle.update("key2", "rrrrrrrrr", gasInfo, leaseInfo);
        bluzelle.delete("key3", gasInfo);
        assertFalse(bluzelle.txHas("key3", gasInfo));
        assertTrue(bluzelle.txHas("key2", gasInfo));
        bluzelle.keys().forEach(System.out::println);
        assertEquals(3, bluzelle.txCount(gasInfo));
        HashMap<String, String> map = new HashMap<>();
        map.put("key2", "booooo");
        map.put("key4", "aqoleg");
        bluzelle.multiUpdate(map, gasInfo);
        bluzelle.txKeys(gasInfo).forEach(System.out::println);
        bluzelle.txKeyValues(gasInfo).forEach((key, value) -> System.out.println(key + ":" + value));
        System.out.println(bluzelle.getLease("key2"));
        bluzelle.renewLease("key2", gasInfo, new LeaseInfo(10, 0, 10, 0));
        System.out.println(bluzelle.txGetLease("key2", gasInfo));
        bluzelle.renewLeaseAll(gasInfo, new LeaseInfo(0, 0, 1, 50));
        System.out.println(bluzelle.txGetLease("Mkey", gasInfo));
        System.out.println(bluzelle.getNShortestLeases(2));
        System.out.println(bluzelle.txGetNShortestLeases(1, gasInfo));
        bluzelle.deleteAll(gasInfo);
    }

    private Bluzelle connect() {
        return Bluzelle.getInstance(
                "bluzelle1upsfjftremwgxz3gfy0wf3xgvwpymqx754ssu9",
                "around buzz diagram captain obtain detail salon mango muffin brother morning jeans display attend" +
                        " knife carry green dwarf vendor hungry fan route pumpkin car",
                "http://testnet.public.bluzelle.com:1317",
                null,
                null
        );
    }
}