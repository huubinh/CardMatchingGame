package com.project.cardmatchingclient.controllers;

import com.project.cardmatchingclient.ClientApplication;
import com.project.cardmatchingclient.network.Connection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateRoomController {

    @FXML
    private Label notification;

    @FXML
    private TextField roomNameInput;

    @FXML
    private BorderPane root;

    @FXML
    private void onExitButtonClicked(ActionEvent event) throws IOException{
        Connection.send("EXIT");
        Platform.exit();
        event.consume();
    }

    @FXML
    private void onCancelButtonClicked(ActionEvent event) throws IOException{
        Stage stage = (Stage) root.getScene().getWindow();
        ClientApplication.changeScene(stage,"fxml/MenuView.fxml");
        event.consume();
    }

    @FXML
    private void onOkButtonClicked(ActionEvent event) throws IOException {
        if (roomNameInput.getText().isEmpty())
            notification.setText("Please enter room's name!");
        else {
            Connection.send("ROOM`CREATE`" + roomNameInput.getText());
            String[] receivedList = Connection.receive().split("`");
            if (receivedList[0].equals("CREATE") && receivedList[1].equals("OK")) {
                Stage stage = (Stage) root.getScene().getWindow();
                ClientApplication.changeScene(stage,"fxml/RoomHostView.fxml");
            }
            else
                notification.setText("Room exists!");
        }
        event.consume();
    }
}
