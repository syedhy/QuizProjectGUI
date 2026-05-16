package com.quizapp.gui.screens;

import com.quizapp.gui.NavigationManager;
import com.quizapp.gui.effects.AnimatedBackground;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SplashScreen extends StackPane {

    public SplashScreen() {
        AnimatedBackground bg = new AnimatedBackground();

        VBox content = new VBox(22);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));

        Label badge = new Label("QUIZ PROJECT");
        badge.getStyleClass().add("splash-badge");

        Label title = new Label("QuizVerse");
        title.getStyleClass().add("splash-title");

        Label subtitle = new Label("Loading your game arena...");
        subtitle.getStyleClass().add("splash-subtitle");

        ProgressBar bar = new ProgressBar(0);
        bar.getStyleClass().add("splash-progress");
        bar.setPrefWidth(520);

        content.getChildren().addAll(badge , title , subtitle , bar);

        getChildren().addAll(bg , content);

        playIntro(content , title , bar);
    }

    private void playIntro(VBox content , Label title , ProgressBar bar) {
        content.setOpacity(0);
        content.setTranslateY(30);
        title.setScaleX(0.92);
        title.setScaleY(0.92);

        FadeTransition fade = new FadeTransition(Duration.millis(800) , content);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(800) , content);
        slide.setFromY(30);
        slide.setToY(0);

        ScaleTransition titleScale = new ScaleTransition(Duration.millis(900) , title);
        titleScale.setFromX(0.92);
        titleScale.setFromY(0.92);
        titleScale.setToX(1.0);
        titleScale.setToY(1.0);

        Timeline progress = new Timeline(
                new KeyFrame(Duration.ZERO , new KeyValue(bar.progressProperty() , 0)),
                new KeyFrame(Duration.millis(2200) , new KeyValue(bar.progressProperty() , 1))
        );

        fade.play();
        slide.play();
        titleScale.play();
        progress.play();

        progress.setOnFinished(e -> {
            FadeTransition out = new FadeTransition(Duration.millis(520) , this);
            out.setFromValue(1);
            out.setToValue(0);

            out.setOnFinished(ev ->
                    NavigationManager.goTo(new ProfileSelectScreen())
            );

            out.play();
        });
    }
}