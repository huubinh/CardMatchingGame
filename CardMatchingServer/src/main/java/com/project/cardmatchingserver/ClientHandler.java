package com.project.cardmatchingserver;

import java.io.*;
import java.net.Socket;
import java.util.*;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private DataInputStream is = null;
    private DataOutputStream os = null;
    private String receivedLine = null;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        System.out.println("Processing: " + socket);
        try {
            is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            loop:
            while (true) {
                String[] receivedList = receive();
                switch (receivedList[0]) {
                    case "SIGNIN":
                        if (checkExistedAccount(receivedList[1], receivedList[2])) {
                            if (checkUsedAccount(receivedList[1]))
                                send("SIGNIN`USED");
                            else {
                                Server.activeUsers.put(socket, receivedList[1]);
                                send("SIGNIN`OK");
                            }
                        } else
                            send("SIGNIN`FAILED");
                        break;
                    case "SIGNUP":
                        if (checkExistedUser(receivedList[1]))
                            send("SIGNUP`FAILED");
                        else {
                            addNewAccount(receivedList[1], receivedList[2]);
                            send("SIGNUP`OK");
                        }
                        break;
                    case "SIGNOUT":
                        Server.activeUsers.remove(socket);
                        break;
                    case "CHPASS":
                        if (changePassword(Server.activeUsers.get(socket), receivedList[1], receivedList[2])) {
                            System.out.println(Server.activeUsers.get(socket));
                            send("CHPASS`OK");
                        } else
                            send("CHPASS`FAILED");
                        break;
                    case "RAND":
                        if (receivedList[1].equals("MATCH")) {
                            if (Server.randomUsers.isEmpty()){
                                Server.randomUsers.put(socket, Server.activeUsers.get(socket));
                            }
                            else {
                                Socket opponentSocket = Server.randomUsers.entrySet().iterator().next().getKey();
                                Server.randomUsers.remove(opponentSocket);
                                String newRandomMatch = Server.activeUsers.get(socket) + "-" + Server.activeUsers.get(opponentSocket);
                                BattleHandler gameHandler = new BattleHandler(socket, opponentSocket, newRandomMatch);
                                Thread newThread = new Thread(gameHandler);
                                newThread.start();
                            }
                        } else if (receivedList[1].equals("CANCEL")) {
                            send("CANCEL`CONFIRM");
                            Server.randomUsers.remove(socket);
                        }
                        break;
                    case "START" :
                        String randomMatchName = receivedList[1];
                        while (Server.battles.contains(randomMatchName));
                        break;
                    case "ROOM" :
                        switch (receivedList[1]) {
                            case "CREATE" -> {
                                String roomName = receivedList[2];
                                if (Server.roomHosts.containsValue(roomName))
                                    send("CREATE`FAILED");
                                else {
                                    Server.roomHosts.put(socket, receivedList[2]);
                                    Server.roomGuests.put(receivedList[2], null);
                                    send("CREATE`OK");
                                }
                            }
                            case "ENTER" -> {
                                if (Server.roomHosts.containsKey(socket)) {
                                    String roomName = Server.roomHosts.get(socket);
                                    send("ENTER`" + roomName);
                                    EmptyRoomHandler emptyRoomHandler = new EmptyRoomHandler(socket, roomName);
                                    Thread thread = new Thread(emptyRoomHandler);
                                    thread.start();
                                    while(Server.roomHosts.containsKey(socket));
                                }
                                else {
                                    String roomName = findRoom(socket);
                                    send("ENTER`" + roomName + "`" + findHost(roomName));
                                    while (Server.roomGuests.get(roomName) != null);
                                }
                            }
                            case "LIST" -> send(loadRoomList());
                            case "JOIN" -> {
                                if (checkRoom(receivedList[2])) {
                                    Server.roomGuests.put(receivedList[2], socket);
                                    send("JOIN`OK");
                                }
                                else
                                    send("JOIN`FAILED");
                            }
                        }
                        break;
                    case "HISTORY" :
                        send(loadHistory(Server.activeUsers.get(socket)));
                        break;
                    case "EXIT":
                        Server.activeUsers.remove(socket);
                        break loop;
                }

            }
        } catch (IOException e) {
            Server.activeUsers.remove(socket);
            System.err.println("Request processing error: " + e);
        }

        System.out.println("Complete processing: " + socket);
    }

    private void send(String string) {
        try {
            System.out.println("Send to " + socket + ": " + string);
            os.writeUTF(string);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] receive() {
        try {
            receivedLine = is.readUTF();
            System.out.println("Received from " + socket + ": " + receivedLine);
            return receivedLine.split("`");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean checkExistedAccount(String username, String password) {

        try {
            FileReader fr = new FileReader(System.getProperty("user.dir") + "/CardMatchingServer/src/main/resources/com/project/cardmatchingserver/db/accounts.txt");
            BufferedReader br = new BufferedReader(fr);
            String line, user, pass;

            while ((line = br.readLine()) != null) {
                user = line.split("`")[0];
                pass = line.split("`")[1];
                if (user.equals(username) && BCrypt.checkpw(password, pass)) {
                    fr.close();
                    br.close();
                    return true;
                }
            }

            fr.close();
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return false;
    }

    private boolean checkUsedAccount(String username) {
        for (Map.Entry activeUser : Server.activeUsers.entrySet()) {
            if (activeUser.getValue().equals(username))
                return true;
        }
        return false;
    }

    private boolean checkExistedUser(String username) {

        try {
            FileReader fr = new FileReader(System.getProperty("user.dir") + "/CardMatchingServer/src/main/resources/com/project/cardmatchingserver/db/accounts.txt");
            BufferedReader br = new BufferedReader(fr);
            String line, user;

            while ((line = br.readLine()) != null) {
                user = line.split("`")[0];
                if (user.equals(username)) {
                    fr.close();
                    br.close();
                    return true;
                }
            }

            fr.close();
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void addNewAccount(String username, String password) {
        try {
            FileWriter fw = new FileWriter(System.getProperty("user.dir") + "/CardMatchingServer/src/main/resources/com/project/cardmatchingserver/db/accounts.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(username + "`" + BCrypt.hashpw(password, BCrypt.gensalt(12)));
            bw.newLine();
            bw.flush();
            fw.close();
            bw.close();
            String path = String.format(System.getProperty("user.dir") + "/CardMatchingServer/src/main/resources/com/project/cardmatchingserver/db/histories/%s.txt", username);
            File history = new File(path);
            history.createNewFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean changePassword(String username, String oldPassword, String newPassword) {
        try {
            String line;
            String path = System.getProperty("user.dir") + "/CardMatchingServer/src/main/resources/com/project/cardmatchingserver/db/accounts.txt";
            File fileToBeModified = new File(path);
            StringBuilder newFile = new StringBuilder();
            BufferedReader reader;

            reader = new BufferedReader(new FileReader(fileToBeModified));
            while ((line = reader.readLine()) != null) {
                if (line.split("`")[0].equals(username)) {
                    if (BCrypt.checkpw(oldPassword, line.split("`")[1]))
                        line = line.replace(line.split("`")[1], BCrypt.hashpw(newPassword, BCrypt.gensalt(12)));
                    else
                        return false;
                }
                newFile.append(line);
                newFile.append(System.lineSeparator());
            }
            fileToBeModified.delete();
            PrintWriter writer = new PrintWriter(path, "UTF-8");
            writer.print(newFile);
            writer.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String loadHistory(String username) throws IOException {
        int matchCounter = 0;
        StringBuilder matchDetails = new StringBuilder();
        FileReader fr = new FileReader(String.format(System.getProperty("user.dir") + "/CardMatchingServer/src/main/resources/com/project/cardmatchingserver/db/histories/%s.txt", username));
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null) {
            matchCounter++;
            matchDetails.append("`" + line);
        }
        return "HISTORY" + "`" + username + "`" + matchCounter + matchDetails;
    }

    public String loadRoomList() {
        int roomCounter = 0;
        StringBuilder roomDetails = new StringBuilder();
        int available = 1;
        for (Map.Entry room : Server.roomHosts.entrySet()) {
            roomCounter++;
            if (Server.roomGuests.get(room.getValue()) != null)
                available = 0;
            roomDetails.append("`" + room.getValue() + "`" + Server.activeUsers.get(room.getKey()) + "`" + available);
        }
        return "LIST`" + roomCounter + roomDetails;
    }

    private String findRoom(Socket guest) {
        for (Map.Entry room : Server.roomGuests.entrySet()) {
            if (room.getValue().equals(guest))
                return room.getKey().toString();
        }
        return null;
    }

    private String findHost(String roomName) {
        Socket host = null;
        for (Map.Entry room : Server.roomHosts.entrySet()) {
            if (room.getValue().equals(roomName)) {
                host = (Socket) room.getKey();
                break;
            }
        }
        return Server.activeUsers.get(host);
    }

    private boolean checkRoom(String roomName) {
        if (Server.roomGuests.containsKey(roomName)) {
            if (Server.roomGuests.get(roomName) != null)
                return false;
            else
                return true;
        }
        else
            return false;
    }
}
