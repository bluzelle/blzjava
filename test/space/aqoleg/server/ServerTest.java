package space.aqoleg.server;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class ServerTest {

    @Test
    void test() {
        Server.main(new String[]{"4000"});

        post("{method:version}");
        post("{method:account}");
        post("{method:create,args:[myKey,myValue,{gas_price:10},{days:10}]}");
        post("{method:read,args:[myKey,true]}");
        post("{method:read,args:[myKey]}");
        post("{method:txRead,args:[myKey,{max_gas:1000}]}");
        post("{method:create,args:[Key,Value]}");
        post("{method:keys}");
        post("{method:txKeys}");
        post("{method:rename,args:[Key,newKey,{gas_price:100}]}");
        post("{method:count}");
        post("{method:tx_count,args:[{gas_price:100}]}");
        post("{method:keyValues}");
        post("{method:tx_key_values}");
        post("{method:getNShortestLeases,args:[12]}");
        post("{method:deleteAll}");
        post("{method:tx_count}");
    }

    private static void post(String request) {
        try {
            URL url = new URL("http://localhost:4000");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(20000);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            OutputStream stream = connection.getOutputStream();
            stream.write(request.getBytes("utf-8"));
            stream.flush();
            stream.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String input;
            StringBuilder builder = new StringBuilder();
            do {
                input = reader.readLine();
                if (input == null) {
                    reader.close();
                    System.out.println(builder.toString());
                    return;
                }
                builder.append(input);
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}