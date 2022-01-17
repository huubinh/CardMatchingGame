package com.project.cardmatchingclient.controllers;

import com.project.cardmatchingclient.ClientApplication;
import com.project.cardmatchingclient.network.Connection;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BattleController {

    @FXML
    private ImageView card0;

    @FXML
    private ImageView card1;

    @FXML
    private ImageView card2;

    @FXML
    private ImageView card3;

    @FXML
    private ImageView card4;

    @FXML
    private ImageView card5;

    @FXML
    private ImageView card6;

    @FXML
    private ImageView card7;

    @FXML
    private ImageView card8;

    @FXML
    private ImageView card9;

    @FXML
    private ImageView card10;

    @FXML
    private ImageView card11;

    @FXML
    private ImageView card12;

    @FXML
    private ImageView card13;

    @FXML
    private ImageView card14;

    @FXML
    private ImageView card15;

    @FXML
    private ImageView card16;

    @FXML
    private ImageView card17;
    
    @FXML
    private Label myScore;

    @FXML
    private Label opponentScore;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextFlow messageBox;

    @FXML
    private TextField chatInput;

    @FXML
    private BorderPane root;

    private static final int NUM_OF_CARDS = 18;
    private String myName = null;
    private String opponentName = null;
    private int turn;
    private int clickCounter = 0;
    private int firstCardId;
    private int firstImageId;
    private List<Integer> matchedCards = new ArrayList<>();

    @FXML
    public void initialize() throws IOException {

        Thread thread = new Thread(() -> {
            try {
                loop:
                while (true) {
                    String receivedLine = Connection.receive();
                    String[] list = receivedLine.split("`");
                    switch (list[0]) {
                        case "START":
                            myName = list[1];
                            opponentName = list[2];
                            Platform.runLater(() -> {
                                Text text = new Text();
                                text.setStyle("-fx-fill: #ff7700;");
                                text.setText(">> " + myName + " has joined <<\n");
                                messageBox.getChildren().add(text);
                                Text text2 = new Text();
                                text2.setStyle("-fx-fill: #ff7700;");
                                text2.setText(">> " + opponentName + " has joined <<\n");
                                messageBox.getChildren().add(text2);
                            });
                            turn = Integer.parseInt(list[3]);
                            if (turn == 1)
                                Platform.runLater(() -> {
                                    Text text = new Text();
                                    text.setStyle("-fx-fill: #ff0066;");
                                    text.setText(">> " + myName + " goes first <<\n");
                                    messageBox.getChildren().add(text);
                                });
                            else if (turn == 2)
                                Platform.runLater(() -> {
                                    Text text = new Text();
                                    text.setStyle("-fx-fill: #ff0066;");
                                    text.setText(">> " + opponentName + " goes first <<\n");
                                    messageBox.getChildren().add(text);
                                });
                            break;
                        case "FLIP":
                            int cardId = Integer.parseInt(list[1]);
                            int imageId = Integer.parseInt(list[2]);
                            Platform.runLater(() -> {
                                flipImage(cardId, imageId);
                            });
                            if (turn == 1) {
                                if (clickCounter == 1) {
                                    firstImageId = imageId;
                                } else if (imageId == firstImageId) {
                                    matchedCards.add(firstCardId);
                                    matchedCards.add(cardId);
                                    clickCounter = 0;
                                    Platform.runLater(() -> {
                                        int newScore = Integer.parseInt(myScore.getText()) + 1;
                                        myScore.setText(Integer.toString(newScore));
                                        Text text = new Text();
                                        text.setStyle("-fx-fill: #007038;");
                                        text.setText(">> " + myName + " got a match <<\n");
                                        messageBox.getChildren().add(text);
                                    });
                                } else {
                                    turn = 2;
                                    clickCounter = 0;
                                    Platform.runLater(() -> {
                                        Text text = new Text();
                                        text.setStyle("-fx-fill: #0062ff;");
                                        text.setText(">> " +opponentName + "'s turn <<\n");
                                        messageBox.getChildren().add(text);
                                        flipImage(firstCardId, 0);
                                        flipImage(cardId, 0);
                                    });
                                }
                            } else {
                                if (clickCounter == 0) {
                                    firstCardId = cardId;
                                    firstImageId = imageId;
                                    clickCounter++;
                                } else if (imageId == firstImageId) {
                                    matchedCards.add(firstCardId);
                                    matchedCards.add(cardId);
                                    clickCounter = 0;
                                    Platform.runLater(() -> {
                                        int newScore = Integer.parseInt(opponentScore.getText()) + 1;
                                        opponentScore.setText(Integer.toString(newScore));
                                        Text text = new Text();
                                        text.setStyle("-fx-fill: #007038");
                                        text.setText(">> " + opponentName + " got a match <<\n");
                                        messageBox.getChildren().add(text);
                                    });
                                } else {
                                    turn = 1;
                                    clickCounter = 0;
                                    Platform.runLater(() -> {
                                        Text text = new Text();
                                        text.setStyle("-fx-fill: #0062ff;");
                                        text.setText(">> " + myName + "'s turn <<\n");
                                        messageBox.getChildren().add(text);
                                        flipImage(firstCardId, 0);
                                        flipImage(cardId, 0);
                                    });
                                }
                            }
                            break;
                        case "CHAT" :
                            Platform.runLater(() -> {
                                Text text = new Text();
                                text.setStyle("-fx-fill: #6a00ff;");
                                text.setText(opponentName + ": " + list[1] + "\n");
                                messageBox.getChildren().add(text);
                            });
                            break;
                        case "QUIT":
                            if (list[1].equals("OPPONENT")) {
                                Platform.runLater(() -> {
                                    try {
                                        Stage stage = (Stage) root.getScene().getWindow();
                                        ClientApplication.changeScene(stage,"fxml/QuitBattleView.fxml");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                            break loop;
                        case "FINISH":
                            if (list[1].equals("WIN"))
                                Platform.runLater(() -> {
                                    Text text = new Text();
                                    text.setStyle("-fx-fill: #ff0000;");
                                    text.setText(">> " + myName + " won <<\n");
                                    messageBox.getChildren().add(text);
                                });
                            else if (list[1].equals("LOSE"))
                                Platform.runLater(() -> {
                                    Text text = new Text();
                                    text.setStyle("-fx-fill: #ff0000;");
                                    text.setText(">> " + opponentName + " won <<\n");
                                    messageBox.getChildren().add(text);
                                });
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();

        scrollPane.vvalueProperty().bind(messageBox.heightProperty());

        chatInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                if(!chatInput.getText().isEmpty()){
                    Text text = new Text();
                    text.setStyle("-fx-fill: #6a00ff;");
                    text.setText(myName + ": " + chatInput.getText() + "\n");
                    messageBox.getChildren().add(text);
                    try {
                        Connection.send("CHAT`" + chatInput.getText());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    chatInput.setText("");
                }
            }
        });
    }

    @FXML
    private void onExitButtonClicked(ActionEvent event) throws IOException{
        Connection.send("QUIT`MATCH");
        Connection.send("EXIT");
        Platform.exit();
        event.consume();
    }

    @FXML
    private void onCard0Clicked(ActionEvent event) throws IOException{
        checkClickedCard(card0);
        event.consume();
    }

    @FXML
    private void onCard1Clicked(ActionEvent event) throws IOException{
        checkClickedCard(card1);
        event.consume();
    }

    @FXML
    private void onCard2Clicked(ActionEvent event) throws IOException{
        checkClickedCard(card2);
        event.consume();
    }

    @FXML
    private void onCard3Clicked(ActionEvent event) throws IOException{
        checkClickedCard(card3);
        event.consume();
    }
    @FXML
    private void onCard4Clicked(ActionEvent event) throws IOException{
        checkClickedCard(card4);
        event.consume();
    }

    @FXML
    private void onCard5Clicked(ActionEvent event) throws IOException{
        checkClickedCard(card5);
        event.consume();
    }

    @FXML
    private void onCard6Clicked(ActionEvent event) throws IOException{
        checkClickedCard(card6);
        event.consume();
    }

    @FXML
    private void onCard7Clicked(ActionEvent event) throws IOException{
        checkClickedCard(card7);
        event.consume();
    }

    @FXML
    private void onCard8Clicked(ActionEvent event) throws IOException{
        checkClickedCard(card8);
        event.consume();
    }

    @FXML
    private void onCard9Clicked(ActionEvent event) throws IOException{
        checkClickedCard(card9);
        event.consume();
    }

    @FXML
    private void onCard10Clicked(ActionEvent event) throws IOException{
        checkClickedCard(card10);
        event.consume();
    }

    @FXML
    private void onCard11Clicked(ActionEvent event) throws IOException{
        checkClickedCard(card11);
        event.consume();
    }

    @FXML
    private void onCard12Clicked(ActionEvent event) throws IOException{
        checkClickedCard(card12);
        event.consume();
    }

    @FXML
    private void onCard13Clicked(ActionEvent event) throws IOException{
        checkClickedCard(card13);
        event.consume();
    }

    @FXML
    private void onCard14Clicked(ActionEvent event) throws IOException{
        checkClickedCard(card14);
        event.consume();
    }

    @FXML
    private void onCard15Clicked(ActionEvent event) throws IOException{
        checkClickedCard(card15);
        event.consume();
    }

    @FXML
    private void onCard16Clicked(ActionEvent event) throws IOException{
        checkClickedCard(card16);
        event.consume();
    }

    @FXML
    private void onCard17Clicked(ActionEvent event) throws IOException{
        checkClickedCard(card17);
        event.consume();
    }

    @FXML
    private void onQuitBattleButtonClicked(ActionEvent event) throws IOException {
        Connection.send("QUIT`BATTLE");
        Stage stage = (Stage) root.getScene().getWindow();
        ClientApplication.changeScene(stage, "fxml/MenuView.fxml");
        event.consume();
    }

    private void flipImage(int cardId, int imageId){
        ImageView cardPosition = convertToCardPosition(cardId);
        File file = new File(System.getProperty("user.dir") + "/CardMatchingClient/src/main/resources/com/project/cardmatchingclient/img/cards/" + imageId +".jpg");
        Image image = new Image(file.toURI().toString(),100, 154, false, false);

        if (imageId == 0) {
            PauseTransition pauseTransition = new PauseTransition(Duration.seconds(0.6));
            pauseTransition.setOnFinished(e -> {
                cardPosition.setDisable(true);
                ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.4), cardPosition);
                scaleTransition.setFromX(1);
                scaleTransition.setToX(-1);
                scaleTransition.play();
                scaleTransition.setOnFinished(event -> {cardPosition.setImage(image); cardPosition.setScaleX(1);});
            });
            pauseTransition.play();
        }
        else {
            cardPosition.setDisable(true);
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.4), cardPosition);
            scaleTransition.setFromX(1);
            scaleTransition.setToX(-1);
            scaleTransition.play();
            scaleTransition.setOnFinished(event -> {cardPosition.setImage(image); cardPosition.setScaleX(1);});
        }
    }

    private void checkClickedCard(ImageView cardPosition) throws IOException {
        int cardId = convertToCardId(cardPosition);
        if (turn == 1 && !matchedCards.contains(cardId)) {
            if (clickCounter == 0) {
                firstCardId = cardId;
                clickCounter++;
                Connection.send("CHOOSE`" + cardId);
            }
            else if (cardId != firstCardId) {
                clickCounter++;
                Connection.send("CHOOSE`" + cardId);
            }
        }
    }

    private ImageView convertToCardPosition(int cardId) {
        switch (cardId) {
            case 0:
                return card0;
            case 1:
                return card1;
            case 2:
                return card2;
            case 3:
                return card3;
            case 4:
                return card4;
            case 5:
                return card5;
            case 6:
                return card6;
            case 7:
                return card7;
            case 8:
                return card8;
            case 9:
                return card9;
            case 10:
                return card10;
            case 11:
                return card11;
            case 12:
                return card12;
            case 13:
                return card13;
            case 14:
                return card14;
            case 15:
                return card15;
            case 16:
                return card16;
            case 17:
                return card17;
        }
        return null;
    }
    
    private int convertToCardId(ImageView cardPosition) {
        switch (cardPosition.getId()) {
            case "card0":
                return 0;
            case "card1":
                return 1;
            case "card2":
                return 2;
            case "card3":
                return 3;
            case "card4":
                return 4;
            case "card5":
                return 5;
            case "card6":
                return 6;
            case "card7":
                return 7;
            case "card8":
                return 8;
            case "card9":
                return 9;
            case "card10":
                return 10;
            case "card11":
                return 11;
            case "card12":
                return 12;
            case "card13":
                return 13;
            case "card14":
                return 14;
            case "card15":
                return 15;
            case "card16":
                return 16;
            case "card17":
                return 17;
        }
        return -1;
    }
}