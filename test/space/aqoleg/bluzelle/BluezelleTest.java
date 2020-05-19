package space.aqoleg.bluzelle;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class BluezelleTest {
    private GasInfo gasInfo = new GasInfo(1000, 0, 0);
    private LeaseInfo leaseInfo = new LeaseInfo(0, 1, 0, 0);

    @Test
    void accounTest() {
        Bluzelle bluzelle = connect();
        System.out.println("version " + bluzelle.version());
        System.out.println("account " + bluzelle.account().toString());

        assertThrows(
                Bluzelle.ServerException.class,
                () -> bluzelle.delete("nonexistingkey", gasInfo)
        );
    }

    @Test
    void crudTest1() {
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
    void crudTest2() {
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
        HashMap<String, String> map = bluzelle.keyValues();
        assertEquals("key1", map.get("key1"));
        assertEquals("34 44 null", map.get("key2"));
        assertEquals("value3", map.get("key3"));
        assertEquals("new value test 4", map.get("key4"));
        assertTrue(bluzelle.has("key1"));
        bluzelle.rename("key1", "Mkey", gasInfo);
        assertFalse(bluzelle.has("key1"));
        bluzelle.update("key2", "rrrrrrrrr", gasInfo, leaseInfo);
        bluzelle.delete("key3", gasInfo);
        assertFalse(bluzelle.txHas("key3", gasInfo));
        assertTrue(bluzelle.txHas("key2", gasInfo));
        ArrayList<String> list = bluzelle.keys();
        assertTrue(list.contains("Mkey"));
        assertTrue(list.contains("key2"));
        assertTrue(list.contains("key4"));
        assertEquals(3, bluzelle.txCount(gasInfo));
        map.clear();
        map.put("key2", "booooo");
        map.put("key4", "aqoleg");
        bluzelle.multiUpdate(map, gasInfo);
        list = bluzelle.txKeys(gasInfo);
        assertTrue(list.contains("key4"));
        assertTrue(list.contains("key2"));
        map = bluzelle.txKeyValues(gasInfo);
        assertEquals("booooo", map.get("key2"));
        assertEquals("aqoleg", map.get("key4"));
        bluzelle.create("key", "lease600", gasInfo, new LeaseInfo(0, 0, 10, 0));
        int lease = bluzelle.getLease("key");
        assertTrue(lease <= 600 && lease > 530);
        bluzelle.renewLeaseAll(gasInfo, new LeaseInfo(0, 0, 1, 40));
        bluzelle.renewLease("key", gasInfo, new LeaseInfo(0, 0, 40, 0));
        lease = bluzelle.txGetLease("key", gasInfo);
        assertTrue(lease <= 2400 && lease > 2200);
        HashMap<String, Integer> leaseMap = bluzelle.getNShortestLeases(3);
        assertEquals(3, leaseMap.size());
        assertTrue(leaseMap.get("Mkey") < 100);
        assertTrue(leaseMap.get("key2") < 100);
        assertTrue(leaseMap.get("key4") < 100);
        leaseMap = bluzelle.txGetNShortestLeases(4, gasInfo);
        assertTrue(leaseMap.get("Mkey") < 100);
        assertTrue(leaseMap.get("key") < 2400);
        assertTrue(leaseMap.get("key2") < 100);
        assertTrue(leaseMap.get("key4") < 100);
        bluzelle.deleteAll(gasInfo);
    }

    private Bluzelle connect() {
        return Bluzelle.connect(
                "around buzz diagram captain obtain detail salon mango muffin brother morning jeans display attend" +
                        " knife carry green dwarf vendor hungry fan route pumpkin car",
                "http://testnet.public.bluzelle.com:1317",
                null,
                null
        );
    }
}