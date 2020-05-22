package space.aqoleg.server;

import org.junit.jupiter.api.Test;
import space.aqoleg.bluzelle.Bluzelle;
import space.aqoleg.bluzelle.Connection;
import space.aqoleg.json.JsonArray;
import space.aqoleg.json.JsonObject;

import static org.junit.jupiter.api.Assertions.*;
import static space.aqoleg.bluzelle.BluzelleTest.endpoint;
import static space.aqoleg.bluzelle.BluzelleTest.mnemonic;

class WrapperTest {

    @Test
    void testExceptions() {
        Wrapper wrapper = new Wrapper();
        wrapper.connect(mnemonic, endpoint, null, null);

        String message = null;
        try {
            wrapper.request("{method:create,args:[10,value]}");
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        }
        assertEquals("Key must be a string", message);

        try {
            wrapper.request("{method:create,args:[key,90]}");
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        }
        assertEquals("Value must be a string", message);

        try {
            wrapper.request("{method:create,args:[key/,value]}");
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        }
        assertEquals("Key cannot contain a slash", message);

        try {
            wrapper.request("{method:create,args:[key,value,{gas_price:90},{days:-9}]}");
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        }
        assertEquals("Invalid lease time", message);

        try {
            wrapper.request("{method:update,args:[10,value]}");
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        }
        assertEquals("Key must be a string", message);

        try {
            wrapper.request("{method:update,args:[key, 10]}");
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        }
        assertEquals("Value must be a string", message);

        try {
            wrapper.request("{method:read,args:[9]}");
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        }
        assertEquals("Key must be a string", message);

        try {
            wrapper.request("{method:txRead,args:[9]}");
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        }
        assertEquals("Key must be a string", message);

        try {
            wrapper.request("{method:delete,args:[9]}");
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        }
        assertEquals("Key must be a string", message);

        try {
            wrapper.request("{method:has,args:[9]}");
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        }
        assertEquals("Key must be a string", message);

        try {
            wrapper.request("{method:txHas,args:[9]}");
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        }
        assertEquals("Key must be a string", message);

        try {
            wrapper.request("{method:rename,args:[10,new]}");
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        }
        assertEquals("Key must be a string", message);

        try {
            wrapper.request("{method:rename,args:[key, 10]}");
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        }
        assertEquals("New key must be a string", message);

        try {
            wrapper.request("{method:multiupdate,args:[[{key:key,value:value},{key:10,value:v}]]}");
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        }
        assertEquals("All keys must be strings", message);

        try {
            wrapper.request("{method:multiupdate,args:[[{key:key,value:value},{key:k,value:1}]]}");
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        }
        assertEquals("All values must be strings", message);

        try {
            wrapper.request("{method:getLease,args:[10]}");
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        }
        assertEquals("Key must be a string", message);

        try {
            wrapper.request("{method:txGetLease,args:[10]}");
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        }
        assertEquals("Key must be a string", message);

        try {
            wrapper.request("{method:renewLease,args:[10]}");
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        }
        assertEquals("Key must be a string", message);

        try {
            wrapper.request("{method:getNShortestLeases,args:[-10]}");
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        }
        assertEquals("Invalid value specified", message);

        try {
            wrapper.request("{method:txGetNShortestLeases,args:[-10]}");
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        }
        assertEquals("Invalid value specified", message);
    }

    @Test
    void test() {
        Wrapper wrapper = new Wrapper();

        assertThrows(
                NullPointerException.class,
                () -> wrapper.connect(null, endpoint, null, null)
        );
        assertThrows(
                Connection.ConnectionException.class,
                () -> wrapper.connect(mnemonic, "http://test.com:1111", null, null)
        );

        assertThrows(
                NullPointerException.class,
                () -> wrapper.request(null)
        );
        // not connected
        assertThrows(
                UnsupportedOperationException.class,
                () -> wrapper.request("{method:deleteAll}")
        );
        wrapper.request("{method:connect,args:[\"" + mnemonic + "doo\",\"" + endpoint + "\"]}");
        // unknown method
        assertThrows(
                UnsupportedOperationException.class,
                () -> wrapper.request("{method:nomethod}")
        );
        // unknown address
        assertThrows(
                Bluzelle.ServerException.class,
                () -> wrapper.request("{method:delete_all}")
        );

        wrapper.connect(mnemonic, endpoint, null, null);

        String result = wrapper.request("{method:version}");
        System.out.println("version");
        System.out.println(result);
        System.out.println();

        wrapper.request("{method:deleteAll,args:[]}");

        result = wrapper.request("{method:account,args:[]}");
        System.out.println("account");
        System.out.println(result);
        System.out.println();

        String s = " !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
        JsonObject json = new JsonObject().put("method", "create");
        json.put("args", new JsonArray().put("key").put(s));
        wrapper.request(json.toString());
        assertThrows(
                Bluzelle.ServerException.class,
                () -> wrapper.request("{method:create,args:[key,value]}")
        );
        assertThrows(
                Bluzelle.ServerException.class,
                () -> wrapper.request("{method:create,args:[key,value],{max_fee:10}]}")
        );
        assertEquals(s, wrapper.request("{method:read,args:[key]}"));
        assertEquals(s, wrapper.request("{method:tx_read,args:[key]}"));
        wrapper.request("{method:update,args:[key,new value,{gas_price:1000}]}");
        assertEquals("false", wrapper.request("{method:has,args:[nokey]}"));
        assertEquals("true", wrapper.request("{method:tx_has,args:[key]}"));
        wrapper.request("{method:rename,args:[key,key2]}");
        assertNull(wrapper.request("{method:read,args:[key]}"));
        wrapper.request("{method:create,args:[key1,value,{gas_price:1000},{minutes:1}]}");
        wrapper.request("{method:multiUpdate,args:[[{key:key1,value:value11},{key:key2,value:value22}]]}");

        result = wrapper.request("{method:txKeyValues,args:[]}");
        assertEquals(
                "[{\"key\":\"key1\",\"value\":\"value11\"},{\"key\":\"key2\",\"value\":\"value22\"}]",
                result
        );

        result = wrapper.request("{method:getNShortestLeases,args:[2]}");
        System.out.println("get 2 shortest leases");
        System.out.println(result);
        System.out.println();

        wrapper.request("{method:deleteAll,args:[]}");
    }
}