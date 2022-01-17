package com.project.cardmatchingserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    public static final int SERVER_PORT = 8080;
    public static HashMap<Socket, String> activeUsers = new HashMap<>();
    public static HashMap<Socket, String> randomUsers = new HashMap<>();
    public static List<String> battles = new ArrayList<String>();
    public static HashMap<Socket, String> roomHosts = new HashMap<>();
    public static HashMap<String, Socket> roomGuests = new HashMap<>();

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = null;

        System.out.println("Binding to port " + SERVER_PORT + ", please wait...");
        serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress("0.0.0.0", SERVER_PORT));
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
    public static String loadRoomList() {
        int roomCounter = 0;
        StringBuilder roomDetails = new StringBuilder();
        int available = 1;
        for (Map.Entry room : roomHosts.entrySet()) {
            roomCounter++;
            if (roomGuests.get(room.getValue()) != null)
                available = 0;
            roomDetails.append("`" + room.getValue() + "`" + activeUsers.get(room.getKey()) + "`" + available);
        }
        return "LIST`" + roomCounter + roomDetails;
    }

    public static String findRoom(Socket guest) {
        for (Map.Entry room : roomGuests.entrySet()) {
            if (room.getValue().equals(guest))
                return room.getKey().toString();
        }
        return null;
    }

    public static String findHost(String roomName) {
        Socket host = null;
        for (Map.Entry room : roomHosts.entrySet()) {
            if (room.getValue().equals(roomName)) {
                host = (Socket) room.getKey();
                break;
            }
        }
        return activeUsers.get(host);
    }

    public static boolean checkRoom(String roomName) {
        if (roomGuests.containsKey(roomName)) {
            if (roomGuests.get(roomName) != null)
                return false;
            else
                return true;
        }
        else
            return false;
    }
}

