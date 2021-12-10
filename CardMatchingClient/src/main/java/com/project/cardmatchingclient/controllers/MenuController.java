package com.project.cardmatchingclient.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class MenuController {

    @FXML
    private Button logoutButton;

    @FXML
    protected void onLogoutButtonClicked(ActionEvent event) throws IOException {

        SceneController.changeScene(event,"fxml/login-view.fxml");
    }

    public void onChangePassWordButtonClicked(ActionEvent event) throws IOException {
        SceneController sceneController = new SceneController();
        sceneController.changeScene(event,"fxml/change-password-view.fxml");
    }
}
