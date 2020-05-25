// http server for bluzelle client using thin wrapper
// usage:
//    Server.main();
//    Server.main(portNumber);
package com.bluzelle.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Server implements HttpHandler {
    private Wrapper wrapper = new Wrapper();

    public static void main(String[] args) {
        int port = 5000;
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception ignored) {
        }

        new Server().init(port);
    }

    @Override
    public void handle(HttpExchange http) {
        String request;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(http.getRequestBody()));
            StringBuilder builder = new StringBuilder();
            do {
                request = reader.readLine();
                if (request == null) {
                    reader.close();
                    request = builder.toString();
                    break;
                }
                builder.append(request);
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
            http.close();
            return;
        }

        boolean error = false;
        String result;
        try {
            if (request.isEmpty()) {
                result = "bluzelle version " + wrapper.request("{method:version}");
            } else {
                result = wrapper.request(request);
            }
        } catch (Exception e) {
            error = true;
            result = e.getMessage();
            if (result.contains("FileNotFoundException")) {
                result = "key not found";
            }
        }

        try {
            if (result == null) {
                http.sendResponseHeaders(200, -1);
            } else {
                byte[] response = result.getBytes("utf-8");
                http.sendResponseHeaders(error ? 400 : 200, response.length);
                OutputStream stream = http.getResponseBody();
                stream.write(response);
                stream.flush();
                stream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            http.close();
        }
    }

    private void init(int port) {
        System.out.println("blzjava 0.4.0");

        String mnemonic = System.getenv("MNEMONIC");
        String endpoint = System.getenv("ENDPOINT");
        String uuid = System.getenv("UUID");
        String chainId = System.getenv("CHAIN_ID");
        System.out.println("\"MNEMONIC\" " + mnemonic);
        System.out.println("\"ENDPOINT\" " + endpoint);
        System.out.println("\"UUID\" " + uuid);
        System.out.println("\"CHAIN_ID\" " + chainId);
        if (mnemonic != null) {
            try {
                wrapper.connect(mnemonic, endpoint, uuid, chainId);
                System.out.println("connected");
            } catch (Exception e) {
                System.out.println("not connected " + e.getMessage());
            }
        }

        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        server.createContext("/", this);
        server.setExecutor(null);
        server.start();

        System.out.println("server started");
        System.out.println("listening for connections on port " + port + " ...");
    }
}