package com.project.cardmatchingclient.controllers;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class HistoryController implements Initializable {
    ObservableList list = FXCollections.observableArrayList();
    @FXML
    private ListView<String> moveList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loadHistory("1");
            updateHistory("3", "test3", "3", "9");
            updateHistory("1", "test3", "3", "9");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateHistory(String userName, String opponentName, String myScore, String opponentScore) throws IOException {
        String path = "C:\\Users\\Tai\\Desktop\\CardMatchingGame\\CardMatchingClient\\src\\main\\java\\com\\project\\cardmatchingclient\\db\\histories\\";
        BufferedWriter out = new BufferedWriter(new FileWriter(path + userName + ".txt", true));
        out.write(opponentName + " " + myScore + " " + opponentScore + " " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(new Date()));
        out.newLine();
        out.close();
    }

    public void loadHistory(String userName) throws IOException {
        list.removeAll(list);
        FileReader fr = new FileReader(String.format("C:\\Users\\Tai\\Desktop\\CardMatchingGame\\CardMatchingClient\\src\\main\\java\\com\\project\\cardmatchingclient\\db\\histories\\%s.txt", userName));
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null) {
            list.add(line);
        }
        moveList.getItems().addAll(list);
    }

    public void onBackButton(ActionEvent event) throws IOException {
        SceneController.changeScene(event, "fxml/menu-view.fxml");
    }
}
