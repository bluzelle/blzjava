package space.aqoleg.bluzelle;

import org.junit.jupiter.api.Test;
import space.aqoleg.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ConnectionTest {

    @Test
    void getTest() {
        assertThrows(
                Connection.EndpointException.class,
                () -> new Connection("net.public").get("")
        );

        Connection connection = new Connection("http://testnet.public.bluzelle.com:1317");
        assertThrows(
                Connection.NullException.class,
                () -> connection.get("")
        );
        System.out.println(connection.get("/node_info"));
    }

    @Test
    void postTest() {
        assertThrows(
                Connection.EndpointException.class,
                () -> new Connection("net.public").post("", false, new JsonObject())
        );

        Connection connection = new Connection("http://testnet.public.bluzelle.com:1317");
        assertThrows(
                Connection.NullException.class,
                () -> connection.post("", false, new JsonObject())
        );
        System.out.println(connection.get("/crud/count/bluzelle1upsfjftremwgxz3gfy0wf3xgvwpymqx754ssu9"));
    }
}