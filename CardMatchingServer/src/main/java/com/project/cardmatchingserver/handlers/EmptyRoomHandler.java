package com.project.cardmatchingserver.handlers;

import com.project.cardmatchingserver.Server;

import java.io.*;
import java.net.Socket;

public class EmptyRoomHandler implements Runnable {

    private Socket hostSocket;
    private DataInputStream hostIs = null;
    private DataOutputStream hostOs = null;
    private String roomName;
    private String hostReceivedLine = null;


    public EmptyRoomHandler(Socket hostSocket, String roomName) {
        this.hostSocket = hostSocket;
        try {
            hostIs = new DataInputStream(new BufferedInputStream(hostSocket.getInputStream()));
            hostOs = new DataOutputStream(new BufferedOutputStream(hostSocket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.roomName = roomName;
    }

    @Override
    public void run() {

//        while (Server.roomGuests.get(roomName) == null);
//        if (Server.roomGuests.get(roomName) != null) {
//
//            while (Server.roomGuests.get(roomName) != null);
//            thread.start();
//            while (Server.roomGuests.containsKey(roomName) && Server.roomGuests.get(roomName) == null);
//        }

        loop:
        while (Server.roomHosts.containsKey(hostSocket)) {

            Thread thread = new Thread(() -> {
                String[] receivedList = receiveFromHost();
                if (receivedList[0].equals("ROOM") && receivedList[1].equals("DELETE")) {
                    Server.roomGuests.remove(Server.roomHosts.get(hostSocket));
                    Server.roomHosts.remove(hostSocket);
                    sendToHost("DELETE`CONFIRM");
                }
            });
            thread.start();

            while (Server.roomGuests.get(roomName) == null) ;
            sendToHost("JOIN`" + Server.activeUsers.get(Server.roomGuests.get(roomName)));
            FullRoomHandler roomHandler = new FullRoomHandler(hostSocket, Server.roomGuests.get(roomName));
            Thread newThread = new Thread(roomHandler);
            newThread.start();
            while (Server.roomGuests.get(roomName) != null) ;

        }

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
}