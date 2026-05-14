package com.quizapp.gui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class StatCard extends VBox {

    private final Label valueLabel;
    private final Label titleLabel;

    public StatCard(String title , String value) {
        setSpacing(10);
        setAlignment(Pos.CENTER);

        getStyleClass().add("stat-card");

        valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");

        titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");

        getChildren().addAll(valueLabel , titleLabel);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}