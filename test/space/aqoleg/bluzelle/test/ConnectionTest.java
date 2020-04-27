package space.aqoleg.bluzelle.test;

import org.junit.jupiter.api.Test;
import space.aqoleg.bluzelle.Connection;
import space.aqoleg.bluzelle.EndpointException;
import space.aqoleg.bluzelle.NullException;
import space.aqoleg.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ConnectionTest {

    @Test
    void getTest() {
        assertThrows(
                EndpointException.class,
                () -> new Connection("net.public").get("")
        );

        Connection connection = new Connection("http://testnet.public.bluzelle.com:1317");
        assertThrows(
                NullException.class,
                () -> connection.get("")
        );
        System.out.println(connection.get("/node_info"));
    }

    @Test
    void postTest() {
        assertThrows(
                EndpointException.class,
                () -> new Connection("net.public").post("", false, new JsonObject())
        );

        Connection connection = new Connection("http://testnet.public.bluzelle.com:1317");
        assertThrows(
                NullException.class,
                () -> connection.post("", false, new JsonObject())
        );
        System.out.println(connection.get("/crud/count/bluzelle1upsfjftremwgxz3gfy0wf3xgvwpymqx754ssu9"));
    }
}