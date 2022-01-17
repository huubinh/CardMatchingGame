package com.project.cardmatchingclient;

import com.project.cardmatchingclient.network.Connection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class ClientApplication extends Application {

    private static double xOffset;
    private static double yOffset;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root;
        if (Connection.initialize()) {
            root = FXMLLoader.load(ClientApplication.class.getResource("fxml/SignInView.fxml"));
//            root = FXMLLoader.load(ClientApplication.class.getResource("fxml/BattleView.fxml"));

        } else {
            root = FXMLLoader.load(ClientApplication.class.getResource("fxml/ServerErrorView.fxml"));
        }
        Scene scene = createScene(stage, root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }

    private static Scene createScene(Stage stage, Parent root) {
        Scene scene = new Scene(root);
        scene.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
            event.consume();
        });
        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
            event.consume();
        });
//        scene.getRoot().setEffect(new DropShadow(10, Color.rgb(100, 100, 100)));
//        scene.getRoot().setEffect(new DropShadow(8, Color.rgb(10, 169, 194)));
        scene.getRoot().setEffect(new DropShadow(6, Color.rgb(233, 171, 255)));

        scene.setFill(Color.TRANSPARENT);
        return scene;
    }

    public static void changeScene(Stage stage, String fxml) throws IOException {
        Parent newRoot = FXMLLoader.load(ClientApplication.class.getResource(fxml));
        Scene scene = createScene(stage, newRoot);
        stage.setScene(scene);
        stage.show();
    }

//    public static void newWindow(Stage currentStage, String afterClosedFxml, String newFxml) throws IOException {
//        Parent root = FXMLLoader.load(ClientApplication.class.getResource(newFxml));
//        Stage newStage = new Stage();
//        Scene newScene = createScene(newStage, root);
//        newStage.setScene(newScene);
//        newStage.initStyle(StageStyle.TRANSPARENT);
//        newStage.show();
//        newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent e) {
//                Platform.exit();
//            }
//        });
//    }

    public static void main(String[] args) {
        launch();
    }
}
