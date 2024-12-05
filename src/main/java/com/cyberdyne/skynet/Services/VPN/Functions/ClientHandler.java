package com.cyberdyne.skynet.Services.VPN.Functions;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter clientWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

            // Read the HTTP request from the client
            String requestLine = clientReader.readLine();
            StringTokenizer tokenizer = new StringTokenizer(requestLine);
            String method = tokenizer.nextToken();
            String url = tokenizer.nextToken();
            String version = tokenizer.nextToken();

            System.out.println("Request: " + method + " " + url + " " + version);

            // Extract the host and port from the URL
            String host = "";
            int port = 80; // Default HTTP port
            if (url.startsWith("http://")) {
                url = url.substring(7);
                int slashIndex = url.indexOf('/');
                if (slashIndex != -1) {
                    host = url.substring(0, slashIndex);
                    url = url.substring(slashIndex);
                } else {
                    host = url;
                    url = "/";
                }
                int colonIndex = host.indexOf(':');
                if (colonIndex != -1) {
                    port = Integer.parseInt(host.substring(colonIndex + 1));
                    host = host.substring(0, colonIndex);
                }
            }

            // Connect to the destination server
            Socket serverSocket = new Socket(host, port);
            PrintWriter serverWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream())));
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

            // Forward the client's request to the destination server
            serverWriter.println(requestLine);
            String headerLine;
            while ((headerLine = clientReader.readLine()) != null && headerLine.length() > 0) {
                serverWriter.println(headerLine);
            }
            serverWriter.println();
            serverWriter.flush();

            // Forward the server's response to the client
            String responseLine;
            while ((responseLine = serverReader.readLine()) != null) {
                clientWriter.println(responseLine);
            }
            clientWriter.flush();

            // Close connections
            serverSocket.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
