package space.aqoleg.bluzelle;

import space.aqoleg.json.JsonObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

@SuppressWarnings("WeakerAccess")
public class Connection {
    private final String endpoint;

    /**
     * @param endpoint http url of the endpoint, for example "http://testnet.com:1317"
     * @throws NullPointerException if endpoint == null
     */
    public Connection(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * perform get request
     *
     * @param path url path
     * @return response String
     * @throws ConnectionException if can not connect
     */
    public String get(String path) {
        try {
            URL url = new URL(endpoint + path);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String input;
            StringBuilder builder = new StringBuilder();
            do {
                input = reader.readLine();
                if (input == null) {
                    reader.close();
                    return builder.toString();
                }
                builder.append(input);
            } while (true);
        } catch (FileNotFoundException e) {
            throw new ConnectionException(e, true);
        } catch (IOException e) {
            throw new ConnectionException(e);
        }
    }

    /**
     * perform post or delete request
     *
     * @param path   url path
     * @param delete if true performs delete request
     * @param data   JsonObject with data to pass with request
     * @return response String
     * @throws ConnectionException if can not connect
     */
    public String post(String path, boolean delete, JsonObject data) {
        try {
            URL url = new URL(endpoint + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);
            connection.setRequestMethod(delete ? "DELETE" : "POST");
            connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

            OutputStream stream = connection.getOutputStream();
            stream.write(data.toString().getBytes("utf-8"));
            stream.flush();
            stream.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String input;
            StringBuilder builder = new StringBuilder();
            do {
                input = reader.readLine();
                if (input == null) {
                    reader.close();
                    return builder.toString();
                }
                builder.append(input);
            } while (true);
        } catch (IOException e) {
            throw new ConnectionException(e);
        }
    }

    public class ConnectionException extends RuntimeException {
        final boolean notFound;

        private ConnectionException(Exception e, boolean notFound) {
            super(e);
            this.notFound = notFound;
        }

        private ConnectionException(Exception e) {
            super(e);
            this.notFound = false;
        }
    }
}