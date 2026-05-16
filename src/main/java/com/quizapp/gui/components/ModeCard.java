package com.quizapp.gui.components;

import com.quizapp.gui.AppSettings;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class ModeCard extends VBox {

    public ModeCard(String icon , String title , String description , String tag) {

        setSpacing(10);
        setPadding(new Insets(18));
        setAlignment(Pos.TOP_LEFT);

        getStyleClass().add("mode-card");

        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("mode-icon");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("mode-title");

        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("mode-description");

        descLabel.setWrapText(true);
        descLabel.setTextOverrun(OverrunStyle.CLIP);

        descLabel.setMaxWidth(240);

        descLabel.setMinHeight(64);
        descLabel.setPrefHeight(64);

        Region spacer = new Region();
        VBox.setVgrow(spacer , Priority.ALWAYS);

        Label tagLabel = new Label(tag);
        tagLabel.getStyleClass().add("mode-tag");

        getChildren().addAll(
                iconLabel,
                titleLabel,
                descLabel,
                spacer,
                tagLabel
        );

        if (AppSettings.isAnimationsEnabled()) {
            addHoverAnimation();
        }
    }

    private void addHoverAnimation() {

        setOnMouseEntered(e -> {

            ScaleTransition scale =
                    new ScaleTransition(
                            Duration.millis(160),
                            this
                    );

            scale.setToX(1.035);
            scale.setToY(1.035);

            TranslateTransition lift =
                    new TranslateTransition(
                            Duration.millis(160),
                            this
                    );

            lift.setToY(-8);

            scale.play();
            lift.play();
        });

        setOnMouseExited(e -> {

            ScaleTransition scale =
                    new ScaleTransition(
                            Duration.millis(160),
                            this
                    );

            scale.setToX(1);
            scale.setToY(1);

            TranslateTransition lift =
                    new TranslateTransition(
                            Duration.millis(160),
                            this
                    );

            lift.setToY(0);

            scale.play();
            lift.play();
        });
    }
}