// connect to the endpoint
// usage:
//    Connection connection = new Connection(endpointString);
//    String response = connection.get(pathString);
//    String response = connection.post(pathString, isDelete, dataJsonObject);
package com.bluzelle;

import com.bluzelle.json.JsonObject;

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
        if (endpoint == null) {
            throw new NullPointerException("null endpoint");
        }
        this.endpoint = endpoint;
    }

    /**
     * perform get request
     *
     * @param path url path
     * @return response String
     * @throws KeyNotFoundException if key does not exist
     * @throws ConnectionException  if can not connect
     */
    public String get(String path) {
        try {
            URL url = new URL(endpoint + path);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String input;
            do {
                input = reader.readLine();
                if (input == null) {
                    reader.close();
                    return builder.toString();
                }
                builder.append(input);
            } while (true);
        } catch (FileNotFoundException e) {
            throw new KeyNotFoundException();
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
            connection.setConnectTimeout(5000);
            connection.setDoOutput(true);
            connection.setRequestMethod(delete ? "DELETE" : "POST");
            connection.setRequestProperty("Content-type", "application/json");

            OutputStream stream = connection.getOutputStream();
            stream.write(data.toString().getBytes("utf-8"));
            stream.flush();
            stream.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String input;
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
        private ConnectionException(Exception e) {
            super(e);
        }
    }

    public static class KeyNotFoundException extends RuntimeException {
        private KeyNotFoundException() {
            super("key not found");
        }
    }
}