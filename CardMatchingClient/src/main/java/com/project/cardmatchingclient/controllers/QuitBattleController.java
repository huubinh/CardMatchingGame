package com.project.cardmatchingclient.controllers;

import com.project.cardmatchingclient.ClientApplication;
import com.project.cardmatchingclient.network.Connection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class QuitBattleController {

    @FXML
    BorderPane root;

    @FXML
    private void onExitButtonClicked(ActionEvent event) throws IOException {
        Connection.send("QUIT`CONFIRM");
        Connection.send("EXIT");
        Platform.exit();
        event.consume();
    }

    @FXML
    private void onOkButtonClicked() throws IOException{
        Connection.send("QUIT`CONFIRM");
        Stage stage = (Stage) root.getScene().getWindow();
        ClientApplication.changeScene(stage,"fxml/MenuView.fxml");
    }
}
