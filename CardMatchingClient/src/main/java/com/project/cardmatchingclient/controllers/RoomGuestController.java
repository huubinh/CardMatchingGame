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

    @FXML
    public void initialize() throws IOException {
        Connection.send("ROOM`ENTER");
        String[] receivedList = Connection.receive().split("`");
        if (receivedList[0].equals("ENTER")) {
            roomName.setText(receivedList[1]);
            notification.setText("Waiting for " + receivedList[2] + " to start...");
        }

        Thread thread = new Thread(() -> {
            try {
                loop:
                while (true) {
                    String[] list = Connection.receive().split("`");
                    switch (list[0]) {
                        case "DELETE":
                        case "KICK":
                        case "START":
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
//        thread.start();

    }

    @FXML
    private void onExitButtonClicked(ActionEvent event) throws IOException{
        Connection.send("ROOM`QUIT");
        Connection.send("EXIT");
        Platform.exit();
        event.consume();
    }

    @FXML
    private void onQuitButtonClicked(ActionEvent event) throws IOException {
        Connection.send("ROOM`QUIT");
        Stage stage = (Stage) root.getScene().getWindow();
        ClientApplication.changeScene(stage, "fxml/MenuView.fxml");
        event.consume();
    }
}
