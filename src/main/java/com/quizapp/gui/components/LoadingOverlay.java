package com.quizapp.gui.components;

import com.quizapp.gui.AppSettings;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class LoadingOverlay extends StackPane {

    private final Label messageLabel = new Label();
    private final Circle spinner = new Circle(22);

    public LoadingOverlay(String message) {
        getStyleClass().add("loading-overlay");
        setAlignment(Pos.CENTER);
        setVisible(false);
        setOpacity(0);

        spinner.getStyleClass().add("loading-spinner");

        messageLabel.setText(message);
        messageLabel.getStyleClass().add("loading-text");

        VBox box = new VBox(18);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("loading-card");
        box.getChildren().addAll(spinner , messageLabel);

        getChildren().add(box);

        if (AppSettings.isAnimationsEnabled()) {
            RotateTransition rotate = new RotateTransition(Duration.seconds(1.1) , spinner);
            rotate.setByAngle(360);
            rotate.setCycleCount(Animation.INDEFINITE);
            rotate.play();
        }
    }

    public void show() {
        setVisible(true);

        FadeTransition fade = new FadeTransition(Duration.millis(220) , this);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    public void hide() {
        FadeTransition fade = new FadeTransition(Duration.millis(220) , this);
        fade.setFromValue(getOpacity());
        fade.setToValue(0);
        fade.setOnFinished(e -> setVisible(false));
        fade.play();
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }
}