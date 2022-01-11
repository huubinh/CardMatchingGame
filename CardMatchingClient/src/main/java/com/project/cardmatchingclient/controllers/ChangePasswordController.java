package com.project.cardmatchingclient.controllers;

import com.project.cardmatchingclient.ClientApplication;
import com.project.cardmatchingclient.network.Connection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ChangePasswordController {

    @FXML
    private PasswordField oldPasswordInput;

    @FXML
    private PasswordField newPasswordInput;

    @FXML
    private PasswordField retypedNewPasswordInput;

    @FXML
    private Label notification;

    @FXML
    private BorderPane root;

    @FXML
    private void onExitButtonClicked(ActionEvent event) throws IOException{
        Connection.send("EXIT");
        Platform.exit();
        event.consume();
    }

    @FXML
    private void onCancelButtonClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) root.getScene().getWindow();
        ClientApplication.changeScene(stage,"fxml/MenuView.fxml");
        event.consume();
    }

    @FXML
    private void onOkButtonClicked(ActionEvent event) throws IOException {
        if (oldPasswordInput.getText().isEmpty() || newPasswordInput.getText().isEmpty() || retypedNewPasswordInput.getText().isEmpty())
            notification.setText("Please enter all fields!");
        else if (oldPasswordInput.getText().equals(newPasswordInput.getText()))
            notification.setText("New password must be different!");
        else if (!retypedNewPasswordInput.getText().equals(newPasswordInput.getText()))
            notification.setText("Retyped new password is incorrect!");
        else if (changePassword(oldPasswordInput.getText(),newPasswordInput.getText()))
            notification.setText("Password was successfully changed");
        else
            notification.setText("Old password is incorrect!");
        event.consume();
    }

    private boolean changePassword(String oldPassword, String newPassword) throws IOException{
        Connection.send("CHPASS`" + oldPassword + "`" + newPassword);
        String receivedLine = Connection.receive();
        if (receivedLine.equals("CHPASS`OK"))
            return true;
        else if (receivedLine.equals("CHPASS`FAILED"))
            return false;

        return false;
    }
}
