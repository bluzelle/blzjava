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
        System.out.println("Server started.\nListening for connections on port " + port + " ...\n");
    }

    @Override
    public void handle(HttpExchange http) {
        String input;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(http.getRequestBody()));
            StringBuilder builder = new StringBuilder();
            do {
                input = reader.readLine();
                if (input == null) {
                    reader.close();
                    input = builder.toString();
                    break;
                }
                builder.append(input);
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
            http.close();
            return;
        }

        String result;
        if (input.isEmpty()) {
            result = "bluzelle version " + Wrapper.wrap("{method:version}");
        } else {
            result = Wrapper.wrap(input);
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