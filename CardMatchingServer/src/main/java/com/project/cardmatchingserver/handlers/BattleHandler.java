package com.project.cardmatchingserver.handlers;

import com.project.cardmatchingserver.Server;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class BattleHandler implements Runnable{

    private Socket playerASocket;
    private DataInputStream playerAIs = null;
    private DataOutputStream playerAOs = null;
    private String playerAReceivedLine = null;
    private Socket playerBSocket;
    private DataInputStream playerBIs = null;
    private DataOutputStream playerBOs = null;
    private String playerBReceivedLine = null;
    private String matchName;
    private boolean isRandom;

    private static final int NUM_OF_CARDS = 18;
    private static final int NUM_OF_IMAGES = 100;
    private List<Integer> pickedRandomImageIds = new ArrayList<Integer>();
    private List<Integer> cardImageIds = new ArrayList<Integer>();
    private int turn;
    private int cardCounter = 0;
    private int firstCardId = 0;
    private int firstImageId = 0;
    private int playerAScore = 0;
    private int playerBScore = 0;
    private Random random = new Random();

    public BattleHandler(Socket playerASocket, Socket playerBSocket, String newMatch, boolean isRandom) {

        Server.battles.add(newMatch);
        matchName = newMatch;
        this.playerASocket = playerASocket;
        this.playerBSocket = playerBSocket;
        this.isRandom = isRandom;

        try {
            playerAIs = new DataInputStream(new BufferedInputStream(playerASocket.getInputStream()));
            playerAOs = new DataOutputStream(new BufferedOutputStream(playerASocket.getOutputStream()));

            playerBIs = new DataInputStream(new BufferedInputStream(playerBSocket.getInputStream()));
            playerBOs = new DataOutputStream(new BufferedOutputStream(playerBSocket.getOutputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void run() {
        if (isRandom) {
            sendToPlayerA("RAND`OK`" + matchName);
            sendToPlayerB("RAND`OK`" + matchName);
        }
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        for (int i = 0; i < NUM_OF_CARDS/2; i++) {
                int imageId;
                do
                    imageId = random.nextInt(NUM_OF_IMAGES) + 1;
                while (pickedRandomImageIds.contains(imageId));

                pickedRandomImageIds.add(imageId);
                cardImageIds.add(imageId);
                cardImageIds.add(imageId);
            }

            Collections.shuffle(cardImageIds);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 6; j++)
                    System.out.print(cardImageIds.get(i*6+j) + " ");
                System.out.print('\n');
            }

            turn = random.nextInt(2) + 1;

            if (isRandom) {
                sendToPlayerA("START`" + Server.activeUsers.get(playerASocket) + "`" + Server.activeUsers.get(playerBSocket) + "`" + turn + "`RAND");
                sendToPlayerB("START`" + Server.activeUsers.get(playerBSocket) + "`" + Server.activeUsers.get(playerASocket) + "`" + (turn == 1 ? 2 : 1) + "`RAND");
            } else {
                sendToPlayerA("START`" + Server.activeUsers.get(playerASocket) + "`" + Server.activeUsers.get(playerBSocket) + "`" + turn + "`HOST");
                sendToPlayerB("START`" + Server.activeUsers.get(playerBSocket) + "`" + Server.activeUsers.get(playerASocket) + "`" + (turn == 1 ? 2 : 1) + "`GUEST");
            }


        Thread playerAThread = new Thread(() -> {
                    handleGame('A');
            });
            Thread playerBThread = new Thread(() -> {
                    handleGame('B');
            });
            playerAThread.start();
            playerBThread.start();
    }

    private void sendToPlayerA(String string) {
        try {
            System.out.println("Send to player A, " + playerASocket + ": " + string);
            playerAOs.writeUTF(string);
            playerAOs.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] receiveFromPlayerA() {
        try {
            playerAReceivedLine = playerAIs.readUTF();
            System.out.println("Received from player A, " + playerASocket + ": " + playerAReceivedLine);
            return playerAReceivedLine.split("`");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendToPlayerB(String string) {
        try {
            System.out.println("Send to player B, " + playerBSocket + ": " + string);
            playerBOs.writeUTF(string);
            playerBOs.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] receiveFromPlayerB() {
        try {
            playerBReceivedLine = playerBIs.readUTF();
            System.out.println("Received from player B, " + playerBSocket + ": " + playerBReceivedLine);
            return playerBReceivedLine.split("`");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void handleGame(char player) {
        String[] receivedList;
        loop:
        while (true) {
            if (player == 'A')
                receivedList = receiveFromPlayerA();
            else
                receivedList = receiveFromPlayerB();

            switch (receivedList[0]) {
                case "QUIT":
                    if (receivedList[1].equals("BATTLE")) {
                        if (isRandom) {
                            if (player == 'A') {
                                sendToPlayerA("QUIT`CONFIRM");
                                sendToPlayerB("QUIT`OPPONENT");
                            }
                            else {
                                sendToPlayerB("QUIT`CONFIRM");
                                sendToPlayerA("QUIT`OPPONENT");
                            }
                        } else {
                            if (player == 'A') {
                                sendToPlayerA("RETURN`CONFIRM");
                                sendToPlayerB("RETURN`OPPONENT");
                            }
                            else {
                                sendToPlayerB("RETURN`CONFIRM");
                                sendToPlayerA("RETURN`OPPONENT");
                            }
                        }
                    }
                    if (Server.battles.contains(matchName))
                        Server.battles.remove(matchName);
                    break loop;
                case "CHOOSE":
                    int cardId = Integer.parseInt(receivedList[1]);
                    int imageId = cardImageIds.get(cardId);
                    sendToPlayerA("FLIP`" + cardId + "`" + imageId);
                    sendToPlayerB("FLIP`" + cardId + "`" + imageId);
                    if (turn == 1) {
                        if (cardCounter == 0) {
                            firstCardId = cardId;
                            firstImageId = imageId;
                            cardCounter++;
                        } else if (imageId == firstImageId) {
                            playerAScore++;
                            cardCounter = 0;
                        } else {
                            turn = 2;
                            cardCounter = 0;
                        }
                    } else {
                        if (cardCounter == 0) {
                            firstCardId = cardId;
                            firstImageId = imageId;
                            cardCounter++;
                        } else if (imageId == firstImageId) {
                            playerBScore++;
                            cardCounter = 0;
                        } else {
                            turn = 1;
                            cardCounter = 0;
                        }
                    }
                    if ((playerAScore + playerBScore) == NUM_OF_CARDS/2) {
                        if (playerAScore > playerBScore ) {
                            sendToPlayerA("FINISH`WIN");
                            sendToPlayerB("FINISH`LOSE");
                        }
                        else {
                            sendToPlayerB("FINISH`WIN");
                            sendToPlayerA("FINISH`LOSE");
                        }
                    }
                    break;
                case "CHAT":
                    if (player == 'A')
                        sendToPlayerB("CHAT`" + receivedList[1]);
                    else
                        sendToPlayerA("CHAT`" + receivedList[1]);
                    break;
            }
        }
//        if (Server.battles.contains(matchName))
//            Server.battles.remove(matchName);
        try {
            if (player == 'A')
                updateHistory(Server.activeUsers.get(playerASocket), Server.activeUsers.get(playerBSocket), playerAScore, playerBScore);
            else
                updateHistory(Server.activeUsers.get(playerBSocket), Server.activeUsers.get(playerASocket), playerBScore, playerAScore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateHistory(String userName, String opponentName, int userScore, int opponentScore) throws IOException {
        String path = System.getProperty("user.dir") + "/CardMatchingServer/src/main/resources/com/project/cardmatchingserver/db/histories/";
        BufferedWriter out = new BufferedWriter(new FileWriter(path + userName + ".txt", true));
        out.write(opponentName + "`" + userScore + "`" + opponentScore + "`" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        out.newLine();
        out.close();
    }
}
