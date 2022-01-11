package com.project.cardmatchingclient.controllers;

import com.project.cardmatchingclient.ClientApplication;
import com.project.cardmatchingclient.network.Connection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class RandomBattleController {

    @FXML
    private BorderPane root;

    @FXML
    public void initialize() throws IOException {
        Connection.send("RAND`MATCH");
        Thread thread = new Thread(() -> {
                try {
                    String[] receivedList = Connection.receive().split("`");
                    if (receivedList[0].equals("RAND") && receivedList[1].equals("OK")) {
                        String hostSocket = receivedList[2];
                        Connection.send("START`" + hostSocket);
                        Platform.runLater( () -> {
                                try {
                                    Stage stage = (Stage) root.getScene().getWindow();
                                    ClientApplication.changeScene(stage, "fxml/GameView.fxml");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        thread.start();
    }

    @FXML
    private void onExitButtonClicked(ActionEvent event) throws IOException{
        Connection.send("RAND`CANCEL");
        Connection.send("EXIT");
        Platform.exit();
        event.consume();
    }

    @FXML
    private void onCancelButtonClicked(ActionEvent event) throws IOException {
        Connection.send("RAND`CANCEL");
        Stage stage = (Stage) root.getScene().getWindow();
        ClientApplication.changeScene(stage,"fxml/MenuView.fxml");
        event.consume();
    }

//    @FXML
//    private void onFindButtonClicked(ActionEvent event) throws IOException {
//
//    }
}
