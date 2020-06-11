package com.bluzelle;

import com.bluzelle.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ConnectionTest {

    @Test
    void getTest() {
        assertThrows(NullPointerException.class, () -> new Connection(null));
        assertThrows(Connection.ConnectionException.class, () -> new Connection("net.public").get(""));
        Connection connection = new Connection("http://dev.testnet.public.bluzelle.com:1317");
        assertThrows(
                Connection.KeyNotFoundException.class,
                () -> connection.get("")
        );

        System.out.println("/node info");
        System.out.println(connection.get("/node_info"));
        System.out.println();
    }

    @Test
    void postTest() {
        assertThrows(
                Connection.ConnectionException.class,
                () -> new Connection("net.public").post("", false, new JsonObject())
        );

        Connection connection = new Connection("http://dev.testnet.public.bluzelle.com:1317");
        assertThrows(
                Connection.ConnectionException.class,
                () -> connection.post("", false, new JsonObject())
        );

        System.out.println("/crud/count/bluzelle1upsfjftremwgxz3gfy0wf3xgvwpymqx754ssu9");
        System.out.println(connection.get("/crud/count/bluzelle1upsfjftremwgxz3gfy0wf3xgvwpymqx754ssu9"));
        System.out.println();
    }
}