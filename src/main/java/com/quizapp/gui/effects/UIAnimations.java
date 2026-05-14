package com.quizapp.gui.effects;

import com.quizapp.gui.AppSettings;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class UIAnimations {

    public static void fadeIn(Node node) {
        if (!AppSettings.isAnimationsEnabled()) return;

        node.setOpacity(0);

        FadeTransition fade = new FadeTransition(Duration.millis(320) , node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    public static void slideFadeIn(Node node) {
        if (!AppSettings.isAnimationsEnabled()) return;

        node.setOpacity(0);
        node.setTranslateY(18);

        FadeTransition fade = new FadeTransition(Duration.millis(340) , node);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(340) , node);
        slide.setFromY(18);
        slide.setToY(0);

        fade.play();
        slide.play();
    }

    public static void buttonPress(Node node) {
        if (!AppSettings.isAnimationsEnabled()) return;

        ScaleTransition down = new ScaleTransition(Duration.millis(70) , node);
        down.setToX(0.96);
        down.setToY(0.96);

        ScaleTransition up = new ScaleTransition(Duration.millis(90) , node);
        up.setToX(1);
        up.setToY(1);

        down.setOnFinished(e -> up.play());

        down.play();
    }
}