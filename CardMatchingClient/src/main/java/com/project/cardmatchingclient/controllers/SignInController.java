package com.project.cardmatchingclient.controllers;

import com.project.cardmatchingclient.ClientApplication;
import com.project.cardmatchingclient.network.Connection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;


public class SignInController {

    @FXML
    private TextField usernameInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private Label notification;

    @FXML
    private BorderPane root;

    @FXML
    private void onExitButtonClicked(ActionEvent event) throws IOException {
        Connection.send("EXIT");
        Platform.exit();
        event.consume();
    }

    @FXML
    void onSignInButtonClicked(ActionEvent event) throws IOException {

        if (usernameInput.getText().isEmpty() || passwordInput.getText().isEmpty()) {
            notification.setText("Please enter both fields!");
        } else if (checkSignIn(usernameInput.getText(),passwordInput.getText())) {
            Stage stage = (Stage) root.getScene().getWindow();
            ClientApplication.changeScene(stage,"fxml/MenuView.fxml");
        }
        event.consume();
    }

    @FXML
    void onSignUpButtonClicked(ActionEvent event) throws IOException {

        if (usernameInput.getText().isEmpty() || passwordInput.getText().isEmpty()) {
            notification.setText("Please enter both fields!");
        } else if (checkSignUp(usernameInput.getText(), passwordInput.getText())) {
            notification.setText("Signed up successfully");
        } else {
            notification.setText("Account already exists!");
        }
        event.consume();
    }

    private boolean checkSignIn(String username, String password) throws IOException{
        Connection.send("SIGNIN`" + username + "`" + password);
        String receivedLine = Connection.receive();
        switch (receivedLine) {
            case "SIGNIN`OK":
                return true;
            case "SIGNIN`FAILED":
                notification.setText("Incorrect username or password!");
                break;
            case "SIGNIN`USED":
                notification.setText("Someone is using this account!");
                break;
        }

        return false;
    }

    private boolean checkSignUp(String username, String password) throws IOException {
        Connection.send("SIGNUP`" + username + "`" + password);
        String receivedLine = Connection.receive();
        if (receivedLine.equals("SIGNUP`OK"))
            return true;
        else if (receivedLine.equals("SIGNUP`FAILED"))
            return false;

        return false;
    }
}