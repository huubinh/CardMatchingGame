package com.project.cardmatchingclient.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    protected void onLoginButtonClicked(ActionEvent event) throws IOException {

        SceneController.changeScene(event,"fxml/menu-view.fxml");
    }

    @FXML
    protected void onRegisterButtonClicked(ActionEvent event) throws IOException {

    }
}
