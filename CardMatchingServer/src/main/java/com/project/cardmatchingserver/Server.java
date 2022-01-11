package com.project.cardmatchingserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server {

    public static final int SERVER_PORT = 8000;
    public static HashMap<Socket, String> activeUsers = new HashMap<>();
    public static HashMap<Socket, String> randomUsers = new HashMap<>();
    public static List<String> battles = new ArrayList<String>();
    public static HashMap<Socket, String> roomHosts = new HashMap<>();
    public static HashMap<String, Socket> roomGuests = new HashMap<>();

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = null;

        System.out.println("Binding to port " + SERVER_PORT + ", please wait...");
        serverSocket = new ServerSocket(SERVER_PORT);
        System.out.println("Server started");
        System.out.println("Waiting for clients...");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Client accepted: " + socket);

            ClientHandler handler = new ClientHandler(socket);
            Thread newThread = new Thread(handler);
            newThread.start();
        }

    }

}
