package com.project.cardmatchingclient.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.io.*;
import java.util.Scanner;


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
    private PasswordField oldPasswordInput;

    @FXML
    private PasswordField newPasswordInput;

    @FXML
    public PasswordField renewPasswordInput;

    @FXML
    private Label wrongChangePass;

    @FXML
    protected void onLoginButtonClicked(ActionEvent event) throws IOException {
        SceneController sceneController = new SceneController();
        if (usernameInput.getText().isEmpty() || passwordInput.getText().isEmpty()) {
            wrongLogin.setText("Must enter full username and password");
        } else if(checkLogin(usernameInput.getText(),passwordInput.getText())) {
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
        if (usernameInput.getText().isEmpty() || passwordInput.getText().isEmpty()) {
            wrongLogin.setText("Must enter full username and password");
        }
        else if (checkExistedAcc(usernameInput.getText())) {
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

    public void onChangePassButtonClicked(ActionEvent event) throws IOException {
        SceneController sceneController = new SceneController();
        if(oldPasswordInput.getText().isEmpty() || newPasswordInput.getText().isEmpty() || renewPasswordInput.getText().isEmpty()){
            wrongChangePass.setText("Please enter a valid password and try again");
        } else if(!newPasswordInput.getText().equals(renewPasswordInput.getText())) {
            wrongChangePass.setText("New password does not match");
        } else if(!checkLogin("1","1")) {
            wrongChangePass.setText("Old password is incorrect");
        } else {
            String line,l;
            String path = "C:\\Users\\Tai\\Desktop\\CardMatchingGame\\CardMatchingClient\\src\\main\\java\\com\\project\\cardmatchingclient\\db\\account.txt";
            File fileToBeModified = new File(path);
            String newFilePass = "";
            BufferedReader reader = null;
//            FileWriter writer = new FileWriter(fileToBeModified,false);

            reader = new BufferedReader(new FileReader(fileToBeModified));
            while ((line = reader.readLine()) != null) {
                if (line.split(" ")[0].equals("1")) {
                    line = line.replace(line.split(" ")[1], BCrypt.hashpw(newPasswordInput.getText(), BCrypt.gensalt(12)));
                }
                newFilePass = newFilePass + line + System.lineSeparator();
            }
            fileToBeModified.delete();
            PrintWriter writer = new PrintWriter(path, "UTF-8");
            writer.println(newFilePass);
            writer.close();

//            writer.write(newFilePass);
//            writer.close();
            sceneController.changeScene(event, "fxml/menu-view.fxml");
        }
    }

    public void onCancelButtonClicked(ActionEvent event) throws IOException {
        SceneController.changeScene(event, "fxml/menu-view.fxml");
    }
}