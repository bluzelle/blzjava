package space.aqoleg.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Server implements HttpHandler {
    private Wrapper wrapper;

    public static void main(String[] args) {
        int port = 5000;
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception ignored) {
        }

        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.createContext("/", new Server());
        server.setExecutor(null);
        server.start();
    }

    @Override
    public void handle(HttpExchange http) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(http.getRequestBody()));
        String input;
        StringBuilder builder = new StringBuilder();
        try {
            do {
                input = reader.readLine();
                if (input == null) {
                    reader.close();
                    break;
                }
                builder.append(input);
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
            http.close();
        }

        if (wrapper == null) {
            wrapper = Wrapper.initialize();
        }
        String result;
        try {
            result = wrapper.proceed(builder.toString());
        } catch (Exception e) {
            result = e.getMessage();
        }

        try {
            byte[] response = result.getBytes("utf-8");
            http.sendResponseHeaders(200, response.length);
            OutputStream stream = http.getResponseBody();
            stream.write(response);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
            http.close();
        }
    }
}