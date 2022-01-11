package com.project.cardmatchingclient.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ServerErrorController {

    @FXML
    private void onExitButtonClicked(ActionEvent event) {
        Platform.exit();
        event.consume();
    }
}
