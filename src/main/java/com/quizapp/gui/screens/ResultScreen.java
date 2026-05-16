package com.quizapp.gui.screens;

import com.quizapp.gui.NavigationManager;
import com.quizapp.gui.QuizModeConfig;
import com.quizapp.gui.QuizResultData;
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

public class ResultScreen extends StackPane {

    public ResultScreen(QuizModeConfig config , QuizResultData result) {
        AnimatedBackground bg = new AnimatedBackground();

        VBox root = new VBox(28);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        GlassCard card = new GlassCard();

        VBox content = new VBox(24);
        content.setAlignment(Pos.CENTER);

        Label title = new Label(config.getModeName() + " Complete");
        title.getStyleClass().add("result-title-modern");

        Label message = new Label(getMessage(result.getAccuracy()));
        message.getStyleClass().add("result-message-modern");

        Label scoreLabel = new Label(result.getScore() + " / " + result.getTotalQuestions());
        scoreLabel.getStyleClass().add("result-score-modern");

        HBox statsRow = new HBox(18);
        statsRow.setAlignment(Pos.CENTER);

        statsRow.getChildren().addAll(
                new StatCard("Accuracy" , String.format("%.1f %%" , result.getAccuracy())),
                new StatCard("Correct" , String.valueOf(result.getCorrectAnswers())),
                new StatCard("Wrong" , String.valueOf(result.getWrongAnswers()))
        );

        VBox eloBox = new VBox(10);
        eloBox.setAlignment(Pos.CENTER);

        if (config.isRanked() && result.hasEloChange()) {
            Label eloTitle = new Label("Ranked Rating Update");
            eloTitle.getStyleClass().add("elo-result");

            Label eloChange = new Label(
                    result.getOldElo()
                            + "  →  "
                            + result.getNewElo()
                            + "  ("
                            + formatEloChange(result.getEloChange())
                            + ")"
            );
            eloChange.getStyleClass().add("elo-result-large");

            eloBox.getChildren().addAll(eloTitle , eloChange);
        }

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
        actions.getChildren().addAll(dashboard , modes, home);

        content.getChildren().addAll(title , message , scoreLabel , statsRow , eloBox , actions);

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

    private String formatEloChange(int change) {
        if (change > 0) return "+" + change;

        return String.valueOf(change);
    }
}