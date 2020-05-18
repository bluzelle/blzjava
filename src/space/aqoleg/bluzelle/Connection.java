package space.aqoleg.bluzelle;

import space.aqoleg.json.JsonObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@SuppressWarnings("WeakerAccess")
public class Connection {
    private final String endpoint;

    public Connection(String endpoint) {
        this.endpoint = endpoint;
    }

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
        } catch (MalformedURLException e) {
            throw new EndpointException(e);
        } catch (FileNotFoundException e) {
            throw new NullException(e);
        } catch (IOException e) {
            throw new ConnectionException(e);
        }
    }

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
        } catch (MalformedURLException e) {
            throw new EndpointException(e);
        } catch (FileNotFoundException e) {
            throw new NullException(e);
        } catch (IOException e) {
            throw new ConnectionException(e);
        }
    }

    public class ConnectionException extends RuntimeException {
        ConnectionException(Exception e) {
            super(e);
        }
    }

    public class EndpointException extends RuntimeException {
        EndpointException(Exception e) {
            super(e);
        }
    }

    public class NullException extends RuntimeException {
        NullException(Exception e) {
            super(e);
        }
    }
}