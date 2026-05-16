package com.quizapp.gui;

import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class NavigationManager {

    private static Stage stage;

    public static void setStage(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void goTo(Parent root) {
        Scene scene = stage.getScene();

        root.setOpacity(0);
        scene.setRoot(root);

        FadeTransition fade = new FadeTransition(Duration.millis(420) , root);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }
}