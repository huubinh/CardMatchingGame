package com.project.cardmatchingserver;

import java.io.*;
import java.net.Socket;

public class RoomHandler implements Runnable {

    private Socket hostSocket;
    private DataInputStream hostIs = null;
    private DataOutputStream hostOs = null;
    private String hostReceivedLine = null;
    private Socket guestSocket;
    private DataInputStream guestIs = null;
    private DataOutputStream guestOs = null;
    private String guestReceivedLine = null;

    public RoomHandler(Socket hostSocket, Socket guestSocket) {
        this.hostSocket = hostSocket;
        this.guestSocket = guestSocket;

        try {
            hostIs = new DataInputStream(new BufferedInputStream(hostSocket.getInputStream()));
            hostOs = new DataOutputStream(new BufferedOutputStream(hostSocket.getOutputStream()));

            guestIs = new DataInputStream(new BufferedInputStream(guestSocket.getInputStream()));
            guestOs = new DataOutputStream(new BufferedOutputStream(guestSocket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        Thread hostThread = new Thread(() -> {
            loop:
            while (true) {
                String[] receivedList = receiveFromHost();
                if(receivedList[0].equals("ROOM")) {
                    switch (receivedList[1]) {
                        case "DELETE":
                            Server.roomGuests.remove(Server.roomHosts.get(hostSocket));
                            Server.roomHosts.remove(hostSocket);
                            sendToGuest("DELETE");
                            break loop;
                        case "KICK":
                            Server.roomGuests.put(Server.roomHosts.get(hostSocket), null);
                            sendToGuest("KICK");
                            break loop;
                        case "QUIT":
                            Server.roomGuests.put(Server.roomHosts.get(hostSocket), null);
                            break loop;
                        case "START":
                            String battle = Server.activeUsers.get(hostSocket) + "-" + Server.activeUsers.get(guestSocket);
                            GameHandler gameHandler = new GameHandler(hostSocket, guestSocket, battle);
                            Thread thread = new Thread(gameHandler);
                            thread.start();
                            while (Server.battles.contains(battle));
                            break;
                    }
                }
            }
        });
        Thread guestThread = new Thread(() -> {
            String[] receivedList = receiveFromGuest();
            if (receivedList[0].equals("ROOM") && receivedList[1].equals("QUIT"))
                sendToHost("QUIT");
        });
        hostThread.start();
        guestThread.start();
    }

    private void sendToHost(String string) {
        try {
            System.out.println("Send to host, " + hostSocket + ": " + string);
            hostOs.writeUTF(string);
            hostOs.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] receiveFromHost() {
        try {
            hostReceivedLine = hostIs.readUTF();
            System.out.println("Received from host, " + hostSocket + ": " + hostReceivedLine);
            return hostReceivedLine.split("`");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendToGuest(String string) {
        try {
            System.out.println("Send to guest, " + guestSocket + ": " + string);
            guestOs.writeUTF(string);
            guestOs.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] receiveFromGuest() {
        try {
            guestReceivedLine = guestIs.readUTF();
            System.out.println("Received from guest, " + guestSocket + ": " + guestReceivedLine);
            return guestReceivedLine.split("`");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
