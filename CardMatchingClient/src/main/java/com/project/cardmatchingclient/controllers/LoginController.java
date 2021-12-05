package com.project.cardmatchingclient.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private Label wrongLogin;

    @FXML
    protected void onLoginButtonClicked(ActionEvent event) throws IOException {
        SceneController sceneController = new SceneController();
        if(usernameInput.getText().toString().equals("tai") && passwordInput.getText().toString().equals("123")) {
            sceneController.changeScene(event, "fxml/menu-view.fxml");
        } else if(usernameInput.getText().isEmpty() && passwordInput.getText().isEmpty()) {
            wrongLogin.setText("Please enter your data.");
        } else {
            wrongLogin.setText("Wrong username or password!");
        }
    }

    @FXML
    protected void onRegisterButtonClicked(ActionEvent event) throws IOException {

    }
}
