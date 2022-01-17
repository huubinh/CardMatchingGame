package com.project.cardmatchingclient.controllers;

import com.project.cardmatchingclient.ClientApplication;
import com.project.cardmatchingclient.network.Connection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class RoomGuestController {

    @FXML
    private Label roomName;

    @FXML
    private Label notification;

    @FXML
    private BorderPane root;

    private boolean isInRoom;

    @FXML
    public void initialize() throws IOException {
        Connection.send("ROOM`ENTER");
        String[] receivedList = Connection.receive().split("`");
        if (receivedList[0].equals("ENTER")) {
            roomName.setText(receivedList[1]);
            notification.setText("Waiting for " + receivedList[2] + " to start...");
            isInRoom = true;
        }

        Thread thread = new Thread(() -> {
            try {
                loop:
                while (true) {
                    String[] list = Connection.receive().split("`");
                    switch (list[0]) {
                        case "DELETE":
                            Platform.runLater(()->{
                                notification.setText(receivedList[2] + " deleted the room");
                            });
                            isInRoom = false;
                            break loop;
                        case "KICK":
                            Platform.runLater(()-> {
                                notification.setText("Oops! " + receivedList[2] + " kicked you!");
                            });
                            isInRoom = false;
                            break loop;
                        case "QUIT":
                            break loop;
                        case "START":

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        thread.start();

    }

    @FXML
    private void onExitButtonClicked(ActionEvent event) throws IOException{
        if (isInRoom) Connection.send("ROOM`QUIT");
        else Connection.send("QUIT");
        Connection.send("EXIT");
        Platform.exit();
        event.consume();
    }

    @FXML
    private void onQuitButtonClicked(ActionEvent event) throws IOException {
        if (isInRoom) Connection.send("ROOM`QUIT");
        else Connection.send("QUIT");
        Stage stage = (Stage) root.getScene().getWindow();
        ClientApplication.changeScene(stage, "fxml/MenuView.fxml");
        event.consume();
    }
}
