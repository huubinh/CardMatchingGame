package com.project.cardmatchingclient.network;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connection {

//    public final static String SERVER_ADDRESS = "4.tcp.ngrok.io";
//    public final static int SERVER_PORT = 11230;

    public final static String SERVER_IP = "25.26.26.45";
    public final static int SERVER_PORT = 8080;

    private static Socket socket = null;
    private static DataInputStream is = null;
    private static DataOutputStream os = null;

    public static boolean initialize() {

        try {
            socket = new Socket(SERVER_IP, SERVER_PORT); // Connect to server
            System.out.println("Connected: " + socket);

            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());

            return true;
        } catch (IOException ie) {
            System.out.println("Can not connect to server");
            return false;
        }
    }

    public static void send(String s) throws IOException{
        os.writeUTF(s);
        os.flush();
        System.out.println("Send to server: " + s);
    }

    public static String receive() throws IOException{
        String receivedLine = is.readUTF();
        System.out.println("Received from server: " + receivedLine);
        return receivedLine;
    }

}
