package com.quizapp.gui.screens;

import com.quizapp.gui.NavigationManager;
import com.quizapp.gui.components.GlassCard;
import com.quizapp.gui.components.StatCard;
import com.quizapp.gui.effects.AnimatedBackground;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PhoneQuizResultScreen extends StackPane {

    public PhoneQuizResultScreen(int correct , int wrong , int total) {
        AnimatedBackground bg = new AnimatedBackground();

        VBox root = new VBox(28);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        GlassCard card = new GlassCard();

        VBox content = new VBox(24);
        content.setAlignment(Pos.CENTER);

        double accuracy = total == 0 ? 0 : ((double) correct / total) * 100;

        Label title = new Label("Phone Quiz Complete");
        title.getStyleClass().add("result-title-modern");

        Label score = new Label(correct + " / " + total);
        score.getStyleClass().add("result-score-modern");

        Label message = new Label(getMessage(accuracy));
        message.getStyleClass().add("result-message-modern");

        HBox stats = new HBox(18);
        stats.setAlignment(Pos.CENTER);

        stats.getChildren().addAll(
                new StatCard("Accuracy" , String.format("%.1f %%" , accuracy)),
                new StatCard("Correct" , String.valueOf(correct)),
                new StatCard("Wrong" , String.valueOf(wrong))
        );

        HBox actions = new HBox(16);
        actions.setAlignment(Pos.CENTER);

        Button modes = new Button("Choose Mode");
        modes.getStyleClass().add("secondary-button");
        modes.setOnAction(e -> NavigationManager.goTo(new ModeSelectScreen()));

        Button home = new Button("Home");
        home.getStyleClass().add("secondary-button");
        home.setOnAction(e -> NavigationManager.goTo(new HomeScreen()));

        Button dashboard = new Button("Dashboard");
        dashboard.getStyleClass().add("primary-button");
        dashboard.setOnAction(e -> NavigationManager.goTo(new DashboardScreen()));

        actions.getChildren().addAll(dashboard , modes , home);

        content.getChildren().addAll(title , message , score , stats , actions);

        card.getChildren().add(content);
        root.getChildren().add(card);

        getChildren().addAll(bg , root);
    }

    private String getMessage(double accuracy) {
        if (accuracy >= 90) return "Excellent Performance";
        if (accuracy >= 70) return "Great Job";
        if (accuracy >= 50) return "Good Attempt";

        return "Needs Practice";
    }
}