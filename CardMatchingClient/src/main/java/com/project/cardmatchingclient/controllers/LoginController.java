package com.project.cardmatchingclient.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.io.*;


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
        if(checkLogin(usernameInput.getText(),passwordInput.getText())) {
            sceneController.changeScene(event, "fxml/menu-view.fxml");
        } else if(usernameInput.getText().isEmpty() && passwordInput.getText().isEmpty()) {
            wrongLogin.setText("Please enter your data.");
        } else {
            wrongLogin.setText("Wrong username or password!");
        }
    }
    public boolean checkLogin(String userName,String passWord) throws IOException {
        FileReader fr = new FileReader("C:\\Users\\Tai\\Desktop\\CardMatchingGame\\CardMatchingClient\\src\\main\\java\\com\\project\\cardmatchingclient\\db\\account.txt");
        BufferedReader br = new BufferedReader(fr);
        String line,user,pass;

        while ((line = br.readLine()) != null) {
            user = line.split(" ")[0];
            pass = line.split(" ")[1];
            if (user.equals(userName.toLowerCase()) && BCrypt.checkpw(passWord, pass)) {
                return true;
            }
        }
        return false;
    }

    @FXML
    protected void onRegisterButtonClicked(ActionEvent event) throws IOException {
        SceneController sceneController = new SceneController();
        if (checkExistedAcc(usernameInput.getText())) {
            wrongLogin.setText("Account already exists");
        } else {
            String path = "C:\\Users\\Tai\\Desktop\\CardMatchingGame\\CardMatchingClient\\src\\main\\java\\com\\project\\cardmatchingclient\\db\\account.txt";
            BufferedWriter out = new BufferedWriter(new FileWriter(path,true));
            out.write(usernameInput.getText() + " " + BCrypt.hashpw(passwordInput.getText(), BCrypt.gensalt(12)));
            out.newLine();
            out.close();
            sceneController.changeScene(event, "fxml/menu-view.fxml");
        }
    }
    public boolean checkExistedAcc(String userName) throws IOException {
        FileReader fr = new FileReader("C:\\Users\\Tai\\Desktop\\CardMatchingGame\\CardMatchingClient\\src\\main\\java\\com\\project\\cardmatchingclient\\db\\account.txt");
        BufferedReader br = new BufferedReader(fr);
        String line,user;

        while ((line = br.readLine()) != null) {
            user = line.split(" ")[0];
            if (user.equals(userName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}