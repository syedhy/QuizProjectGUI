package com.quizapp.gui.screens;

import com.quizapp.gui.NavigationManager;
import com.quizapp.gui.components.GlassCard;
import com.quizapp.gui.effects.AnimatedBackground;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PvpResultScreen extends StackPane {

    public PvpResultScreen(int playerOneScore , int playerTwoScore , int totalQuestions) {

        AnimatedBackground bg = new AnimatedBackground();

        GlassCard card = new GlassCard();
        card.setMaxWidth(900);

        VBox content = new VBox(28);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(42));

        Label title = new Label("PvP Match Complete");
        title.getStyleClass().add("result-title-modern");

        Label winner = new Label(getWinnerMessage(playerOneScore , playerTwoScore));
        winner.getStyleClass().add("result-message-modern");

        HBox scoresRow = new HBox(24);
        scoresRow.setAlignment(Pos.CENTER);

        VBox playerOneCard = createPlayerCard(
                "Player 1",
                playerOneScore
        );

        VBox playerTwoCard = createPlayerCard(
                "Player 2",
                playerTwoScore
        );

        scoresRow.getChildren().addAll(playerOneCard , playerTwoCard);

        Label total = new Label("Total Questions : " + totalQuestions);
        total.getStyleClass().add("quiz-hud-text");

        HBox buttons = new HBox(18);
        buttons.setAlignment(Pos.CENTER);

        Button modes = new Button("Choose Mode");
        modes.getStyleClass().add("primary-button");

        modes.setOnAction(e ->
                NavigationManager.goTo(new ModeSelectScreen())
        );

        Button home = new Button("Back Home");
        home.getStyleClass().add("secondary-button");

        home.setOnAction(e ->
                NavigationManager.goTo(new HomeScreen())
        );

        Button dashboard = new Button("Dashboard");
        dashboard.getStyleClass().add("secondary-button");
        dashboard.setOnAction(e -> NavigationManager.goTo(new DashboardScreen()));
        buttons.getChildren().addAll(modes , dashboard , home);

        content.getChildren().addAll(
                title,
                winner,
                scoresRow,
                total,
                buttons
        );

        card.getChildren().add(content);

        setAlignment(Pos.CENTER);

        getChildren().addAll(bg , card);
    }

    private VBox createPlayerCard(String player , int score) {

        VBox card = new VBox(14);
        card.setAlignment(Pos.CENTER);

        card.getStyleClass().add("stat-card");

        Label playerLabel = new Label(player);
        playerLabel.getStyleClass().add("stat-title");

        Label scoreLabel = new Label(String.valueOf(score));
        scoreLabel.getStyleClass().add("result-score-modern");

        card.getChildren().addAll(playerLabel , scoreLabel);

        return card;
    }

    private String getWinnerMessage(int playerOneScore , int playerTwoScore) {

        if (playerOneScore > playerTwoScore) {
            return "Player 1 Wins";
        }

        if (playerTwoScore > playerOneScore) {
            return "Player 2 Wins";
        }

        return "It's A Draw";
    }
}