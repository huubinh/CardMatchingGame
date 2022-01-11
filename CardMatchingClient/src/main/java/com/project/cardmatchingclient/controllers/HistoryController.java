package com.project.cardmatchingclient.controllers;

import com.project.cardmatchingclient.ClientApplication;
import com.project.cardmatchingclient.models.HistoryModel;
import com.project.cardmatchingclient.network.Connection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HistoryController {

    @FXML
    private TableView history;

    @FXML
    private TableColumn userNameColumn;

    @FXML
    private TableColumn userScoreColumn;

    @FXML
    private TableColumn opponentScoreColumn;

    @FXML
    private TableColumn opponentNameColumn;

    @FXML
    private TableColumn dateColumn;

    @FXML
    private BorderPane root;

    @FXML
    public void initialize() throws IOException {
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        userScoreColumn.setCellValueFactory(new PropertyValueFactory<>("userScore"));
        opponentScoreColumn.setCellValueFactory(new PropertyValueFactory<>("opponentScore"));
        opponentNameColumn.setCellValueFactory(new PropertyValueFactory<>("opponentName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        history.setPlaceholder(new Label("No matches to show"));

        Connection.send("HISTORY");
        String[] receivedList = Connection.receive().split("`");
        if (receivedList[0].equals("HISTORY")) {
            String userName = receivedList[1];
            int num = Integer.parseInt(receivedList[2]);
            for (int i = 0, j = 3; i < num; i++, j++) {
                int k = i*3 + j;
                HistoryModel matchDetails = new HistoryModel(userName, receivedList[k], receivedList[k+1], receivedList[k+2], receivedList[k+3]);
                history.getItems().add(matchDetails);
            }
        }
    }

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
}
