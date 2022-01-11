package com.project.cardmatchingclient.controllers;

import com.project.cardmatchingclient.ClientApplication;
import com.project.cardmatchingclient.network.Connection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {

    @FXML
    private BorderPane root;

    @FXML
    private void onExitButtonClicked(ActionEvent event) throws IOException{
        Connection.send("EXIT");
        Platform.exit();
        event.consume();
    }

    @FXML
    private void onRandomBattleButtonClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) root.getScene().getWindow();
        ClientApplication.changeScene(stage,"fxml/RandomBattleView.fxml");
        event.consume();
    }

    @FXML
    private void onCreateRoomButtonClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) root.getScene().getWindow();
        ClientApplication.changeScene(stage,"fxml/CreateRoomView.fxml");
        event.consume();
    }

    @FXML
    private void onJoinRoomButtonClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) root.getScene().getWindow();
        ClientApplication.changeScene(stage,"fxml/RoomListView.fxml");
        event.consume();
    }

    @FXML
    private void onHistoryButtonClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) root.getScene().getWindow();
        ClientApplication.changeScene(stage,"fxml/HistoryView.fxml");
        event.consume();
    }

    @FXML
    private void onChangePasswordButtonClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) root.getScene().getWindow();
        ClientApplication.changeScene(stage,"fxml/ChangePasswordView.fxml");
        event.consume();
    }

    @FXML
    private void onSignOutButtonClicked(ActionEvent event) throws IOException {
        Connection.send("SIGNOUT");
        Stage stage = (Stage) root.getScene().getWindow();
        ClientApplication.changeScene(stage,"fxml/SignInView.fxml");
        event.consume();
    }


}
