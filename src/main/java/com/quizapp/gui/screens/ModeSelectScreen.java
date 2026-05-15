package com.quizapp.gui.screens;

import com.quizapp.gui.NavigationManager;
import com.quizapp.gui.QuizModeConfig;
import com.quizapp.gui.components.ModeCard;
import com.quizapp.gui.effects.AnimatedBackground;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ModeSelectScreen extends StackPane {

    public ModeSelectScreen() {

        AnimatedBackground bg = new AnimatedBackground();

        VBox root = new VBox(34);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(40));

        Label title = new Label("Choose Game Mode");
        title.getStyleClass().add("dashboard-title");

        Label subtitle = new Label(
                "Select the challenge that matches your playstyle"
        );

        subtitle.getStyleClass().add("dashboard-subtitle");

        FlowPane cards = new FlowPane();

        cards.setAlignment(Pos.CENTER);

        cards.setHgap(24);
        cards.setVgap(24);

        cards.getChildren().addAll(

                createCard(
                        "⏱",
                        "Timed Mode",
                        "Race against the clock and answer quickly before time runs out",
                        "FAST PACED",
                        QuizModeConfig.timed()
                ),

                createCard(
                        "❤️",
                        "Survival Mode",
                        "You only have 3 lives. Every wrong answer matters",
                        "HIGH PRESSURE",
                        QuizModeConfig.survival()
                ),

                createCard(
                        "💀",
                        "Sudden Death",
                        "One wrong answer instantly ends the run",
                        "EXTREME",
                        QuizModeConfig.suddenDeath()
                ),

                createCard(
                        "🧠",
                        "LLM Mode",
                        "Generate custom AI quizzes on any topic instantly",
                        "AI POWERED",
                        QuizModeConfig.llm()
                ),

                createCard(
                        "🏆",
                        "ELO Ranked",
                        "Compete for rating and climb the leaderboard",
                        "COMPETITIVE",
                        QuizModeConfig.elo()
                ),

                createCard(
                        "⚔",
                        "Player Vs Player",
                        "Battle another player locally on the same device",
                        "MULTIPLAYER",
                        QuizModeConfig.pvp()
                ),

                createPhoneCard(
                        "📱",
                        "Phone QR Mode",
                        "Scan a QR code and answer the quiz from your phone",
                        "MOBILE"
                )
        );

        Button back = new Button("Back Home");
        back.getStyleClass().add("secondary-button");

        back.setOnAction(e ->
                NavigationManager.goTo(new HomeScreen())
        );

        root.getChildren().addAll(
                title,
                subtitle,
                cards,
                back
        );

        getChildren().addAll(bg , root);
    }

    private ModeCard createCard(
            String icon,
            String title,
            String description,
            String tag,
            QuizModeConfig config
    ) {

        ModeCard card =
                new ModeCard(
                        icon,
                        title,
                        description,
                        tag
                );

        card.setOnMouseClicked(e -> {

            if (config.getModeName().equals("LLM Mode")) {

                NavigationManager.goTo(
                        new LLMSetupScreen()
                );

            } else {

                NavigationManager.goTo(
                        new QuizSetupScreen(config)
                );
            }
        });

        return card;
    }

    private ModeCard createPhoneCard(
                    String icon,
                    String title,
                    String description,
                    String tag) {

            ModeCard card = new ModeCard(
                            icon,
                            title,
                            description,
                            tag);

            card.setOnMouseClicked(e -> NavigationManager.goTo(
                            new QuizSetupScreen(
                                            QuizModeConfig.phoneQr())));

            return card;
    }
}