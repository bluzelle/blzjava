package space.aqoleg.server;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static space.aqoleg.bluzelle.BluzelleTest.endpoint;
import static space.aqoleg.bluzelle.BluzelleTest.mnemonic;

class ServerTest {
    private int port = 4100;

    @Test
    void exceptions() {
        Server.main(new String[]{String.valueOf(port)});
        System.out.println();

        post("{method:connect,args:[\"" + mnemonic + "\",\"" + endpoint + "\"]}");
        post("{method:create,args:[10,myValue,{gas_price:10},{days:10}]}");
        post("{method:create,args:[key,10,{gas_price:10},{days:10}]}");
        post("{method:create,args:[key,myValue,{gas_price:10},{days:-10}]}");
        post("{method:create,args:[key//,myValue]}");
        post("{method:rename,args:[Key,100,{gas_price:100}]}");
        post("{method:getNShortestLeases,args:[-12]}");
        post("{method:delete,args:[nonexisting]}");
    }

    @Test
    void test() {
        Server.main(new String[]{String.valueOf(port)});
        System.out.println();

        post("{method:version}");
        post("{method:connect,args:[\"" + mnemonic + "\",\"" + endpoint + "\"]}");
        post("");
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

    private void post(String request) {
        try {
            System.out.println("request " + request);

            URL url = new URL("http://localhost:" + port);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            OutputStream stream = connection.getOutputStream();
            stream.write(request.getBytes("utf-8"));
            stream.flush();
            stream.close();

            int responseCode = connection.getResponseCode();
            System.out.println("response code " + responseCode);

            BufferedReader reader;
            if (responseCode == 200) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String input;
            StringBuilder builder = new StringBuilder();
            do {
                input = reader.readLine();
                if (input == null) {
                    reader.close();
                    System.out.println("response " + builder.toString());
                    System.out.println();
                    return;
                }
                builder.append(input);
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}