package com.project.cardmatchingclient.controllers;

import com.project.cardmatchingclient.ClientApplication;
import com.project.cardmatchingclient.models.HistoryModel;
import com.project.cardmatchingclient.models.RoomListModel;
import com.project.cardmatchingclient.network.Connection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class RoomListController {

    @FXML
    private Label notification;

    @FXML
    private TableView roomList;

    @FXML
    private TableColumn nameColumn;

    @FXML
    private TableColumn hostColumn;

    @FXML
    private TableColumn stateColumn;

    @FXML
    private BorderPane root;

    @FXML
    public void initialize() throws IOException {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        hostColumn.setCellValueFactory(new PropertyValueFactory<>("host"));
        stateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));

        roomList.setPlaceholder(new Label("No rooms to show"));

        roomList.setRowFactory( tableView -> {
            TableRow<RoomListModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    RoomListModel roomDetail = row.getItem();
                    if (roomDetail.getState().equals("Full"))
                        notification.setText("Room is full!");
                    else {
                        try {
                            Connection.send("ROOM`JOIN`" + roomDetail.getName());
                            String[] receivedList = Connection.receive().split("`");
                            if (receivedList[0].equals("JOIN") && receivedList[1].equals("OK")) {
                                Stage stage = (Stage) root.getScene().getWindow();
                                ClientApplication.changeScene(stage,"fxml/RoomGuestView.fxml");
                            }
                            else
                                notification.setText("Can not join room. Please update the room list!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                event.consume();
            });
            return row;
        });

        Connection.send("ROOM`LIST");

        String[] receivedList = Connection.receive().split("`");
        if (receivedList[0].equals("LIST")) {
            int num = Integer.parseInt(receivedList[1]);
            for (int i = 0, j =2; i < num; i++, j++) {
                int k = i*2 + j;
                String state = receivedList[k+2].equals("1") ? "Available" : "Full" ;
                RoomListModel roomDetails = new RoomListModel(receivedList[k], receivedList[k+1], state);
                roomList.getItems().add(roomDetails);
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
    private void onUpdateButtonClicked(ActionEvent event) throws IOException {
        Connection.send("ROOM`LIST");
        String[] receivedList = Connection.receive().split("`");
        if (receivedList[0].equals("LIST")) {
            roomList.getItems().clear();
            int num = Integer.parseInt(receivedList[1]);
            for (int i = 0, j =2; i < num; i++, j++) {
                int k = i*2 + j;
                String state = receivedList[k+2].equals("1") ? "Available" : "Full" ;
                RoomListModel roomDetails = new RoomListModel(receivedList[k], receivedList[k+1], state);
                roomList.getItems().add(roomDetails);
            }
            notification.setText("Updated successfully");
        }
        event.consume();
    }

        @FXML
    private void onCancelButtonClicked(ActionEvent event) throws IOException {
        Stage stage = (Stage) root.getScene().getWindow();
        ClientApplication.changeScene(stage,"fxml/MenuView.fxml");
        event.consume();
    }
}
