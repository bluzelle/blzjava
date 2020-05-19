package space.aqoleg.bluzelle;

import org.junit.jupiter.api.Test;
import space.aqoleg.json.JsonObject;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectionTest {

    @Test
    void getTest() {
        assertThrows(
                Connection.ConnectionException.class,
                () -> new Connection("net.public").get("")
        );

        Connection connection = new Connection("http://testnet.public.bluzelle.com:1317");
        assertThrows(
                Connection.ConnectionException.class,
                () -> connection.get("")
        );
        boolean notFound = false;
        try {
            connection.get("");
        } catch (Connection.ConnectionException e) {
            notFound = e.notFound;
        }
        assertTrue(notFound);
        System.out.println(connection.get("/node_info"));
    }

    @Test
    void postTest() {
        assertThrows(
                Connection.ConnectionException.class,
                () -> new Connection("net.public").post("", false, new JsonObject())
        );

        Connection connection = new Connection("http://testnet.public.bluzelle.com:1317");
        assertThrows(
                Connection.ConnectionException.class,
                () -> connection.post("", false, new JsonObject())
        );
        System.out.println(connection.get("/crud/count/bluzelle1upsfjftremwgxz3gfy0wf3xgvwpymqx754ssu9"));
    }
}