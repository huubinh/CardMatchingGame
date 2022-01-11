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

public class RoomHostController {

    @FXML
    private Label roomName;

    @FXML
    private Label guestName;

    @FXML
    private Button kickButton;

    @FXML
    private Button startButton;

    @FXML
    private BorderPane root;

    @FXML
    public void initialize() throws IOException {
        Connection.send("ROOM`ENTER");
        String[] receivedList = Connection.receive().split("`");
        if (receivedList[0].equals("ENTER"))
            roomName.setText(receivedList[1]);
        kickButton.setDisable(true);
        startButton.setDisable(true);

        Thread thread = new Thread(() -> {
            try {
                String receivedLine = Connection.receive();
                String[] list = receivedLine.split("`");
                if (list[0].equals("JOIN")) {
                    Platform.runLater(() -> {
                        guestName.setText(list[1] + " has joined the room");
                        kickButton.setDisable(false);
                        startButton.setDisable(false);
                    });
                    Connection.send("JOINED");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
    @FXML
    private void onExitButtonClicked(ActionEvent event) throws IOException{
        Connection.send("ROOM`DELETE");
        Connection.send("EXIT");
        Platform.exit();
        event.consume();
    }

    @FXML
    private void onDeleteButtonClicked(ActionEvent event) throws IOException {
        Connection.send("ROOM`DELETE");
        Stage stage = (Stage) root.getScene().getWindow();
        ClientApplication.changeScene(stage, "fxml/MenuView.fxml");
        event.consume();
    }

    @FXML
    private void onKickButtonClicked(ActionEvent event) {

        event.consume();
    }

    @FXML
    private void onStartButtonClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) root.getScene().getWindow();
        ClientApplication.changeScene(stage, "fxml/GameView.fxml");
        event.consume();
    }
}
