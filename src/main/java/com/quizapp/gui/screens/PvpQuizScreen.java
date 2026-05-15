package com.quizapp.gui.screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.quizapp.gui.NavigationManager;
import com.quizapp.gui.QuizModeConfig;
import com.quizapp.gui.components.GlassCard;
import com.quizapp.gui.effects.AnimatedBackground;
import com.quizapp.helpers.Question;
import com.quizapp.profiles.Profile;
import com.quizapp.profiles.ProfileManager;
import com.quizapp.profiles.ProfileSession;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PvpQuizScreen extends StackPane {

    @SuppressWarnings("unused")
    private final QuizModeConfig config;
    private final List<Question> questions;

    private final Label progressLabel = new Label();
    private final Label turnLabel = new Label();
    private final Label scoreLabel = new Label();
    private final Label questionLabel = new Label();
    private final VBox optionsBox = new VBox(18);
    private final ProgressBar progressBar = new ProgressBar();

    private int currentIndex;
    private int playerTurn;
    private int playerOneScore;
    private int playerTwoScore;
    private boolean quizFinished;

    public PvpQuizScreen(QuizModeConfig config , List<Question> questions) {
        this.config = config;
        this.questions = limitQuestions(questions , config.getQuestionLimit());
        this.currentIndex = 0;
        this.playerTurn = 1;
        this.playerOneScore = 0;
        this.playerTwoScore = 0;
        this.quizFinished = false;

        AnimatedBackground bg = new AnimatedBackground();

        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(28));

        GlassCard hudCard = new GlassCard();
        hudCard.setMaxWidth(1100);

        VBox hudContent = new VBox(18);
        hudContent.setAlignment(Pos.CENTER);

        Label title = new Label("Player Vs Player");
        title.getStyleClass().add("quiz-mode-title");

        HBox statsRow = new HBox(28);
        statsRow.setAlignment(Pos.CENTER);

        progressLabel.getStyleClass().add("quiz-hud-text");
        turnLabel.getStyleClass().add("quiz-hud-text");
        scoreLabel.getStyleClass().add("quiz-hud-text");

        statsRow.getChildren().addAll(progressLabel , turnLabel , scoreLabel);

        progressBar.getStyleClass().add("quiz-progress-modern");
        progressBar.setPrefWidth(860);
        progressBar.setPrefHeight(26);

        hudContent.getChildren().addAll(title , statsRow , progressBar);
        hudCard.getChildren().add(hudContent);

        GlassCard questionCard = new GlassCard();
        questionCard.setMaxWidth(1100);

        VBox questionContent = new VBox(18);
        questionContent.setAlignment(Pos.CENTER);

        questionLabel.getStyleClass().add("quiz-question-text");
        questionLabel.setWrapText(true);
        questionLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        questionLabel.setMaxWidth(1000);
        questionLabel.setMinHeight(120);
        questionLabel.setPrefHeight(120);

        optionsBox.setAlignment(Pos.CENTER);
        optionsBox.setMaxWidth(920);

        questionContent.getChildren().addAll(questionLabel , optionsBox);
        questionCard.getChildren().add(questionContent);

        Button exitButton = new Button("Exit PvP");
        exitButton.getStyleClass().add("secondary-button");
        exitButton.setOnAction(e -> NavigationManager.goTo(new ModeSelectScreen()));

        root.getChildren().addAll(hudCard , questionCard , exitButton);
        getChildren().addAll(bg , root);

        if (this.questions.isEmpty()) {
            progressLabel.setText("No questions loaded");
            questionLabel.setText("Question file was not found or is empty");
            return;
        }

        renderQuestion();
    }

    private static List<Question> limitQuestions(List<Question> questions , int limit) {
        if (questions == null || questions.isEmpty()) return new ArrayList<>();

        List<Question> copy = new ArrayList<>(questions);
        Collections.shuffle(copy);

        return new ArrayList<>(copy.subList(0 , Math.min(limit , copy.size())));
    }

    private void renderQuestion() {
        Question q = questions.get(currentIndex);

        progressLabel.setText("Question " + (currentIndex + 1) + " / " + questions.size());
        turnLabel.setText("Player " + playerTurn + "'s Turn");
        scoreLabel.setText("P1 : " + playerOneScore + "     P2 : " + playerTwoScore);

        progressBar.setProgress((double) (currentIndex + 1) / questions.size());

        questionLabel.setText(q.question);
        optionsBox.getChildren().clear();

        for (int i = 0; i < q.options.length; i++) {
            String optionLetter = String.valueOf((char) ('A' + i));
            String optionText = q.options[i];

            Button button = new Button(optionLetter + ". " + optionText);
            button.getStyleClass().add("quiz-option-button");
            button.setMaxWidth(Double.MAX_VALUE);
            button.setMinHeight(96);
            button.setWrapText(true);
            button.setAlignment(Pos.CENTER_LEFT);
            button.setOnAction(e -> handleAnswer(button , optionLetter));

            optionsBox.getChildren().add(button);
        }
    }

    private void handleAnswer(Button clickedButton , String selectedLetter) {
        Question q = questions.get(currentIndex);

        String correctLetter = q.answer.trim().toUpperCase();
        boolean correct = correctLetter.equalsIgnoreCase(selectedLetter);

        if (correct) {
            if (playerTurn == 1) playerOneScore++;
            else playerTwoScore++;
        }

        for (var node : optionsBox.getChildren()) {
            Button button = (Button) node;
            button.setDisable(true);

            if (button.getText().startsWith(correctLetter + ".")) {
                button.getStyleClass().add("correct-option");
            } else if (button == clickedButton && !correct) {
                button.getStyleClass().add("wrong-option");
            }
        }

        delayThenNext();
    }

    private void delayThenNext() {
        new Thread(() -> {
            try {
                Thread.sleep(900);
            } catch (Exception ignored) {
            }

            Platform.runLater(() -> {
                if (currentIndex >= questions.size() - 1) {
                    finishQuiz();
                } else {
                    currentIndex++;
                    playerTurn = playerTurn == 1 ? 2 : 1;
                    renderQuestion();
                }
            });
        }).start();
    }

    private void finishQuiz() {
        if (quizFinished) return;

        quizFinished = true;
        savePvpStats();

        NavigationManager.goTo(
                new PvpResultScreen(
                        playerOneScore,
                        playerTwoScore,
                        questions.size()
                )
        );
    }

    private void savePvpStats() {
        Profile profile = ProfileSession.getCurrentProfile();

        if (profile == null) return;

        profile.setTotalGames(profile.getTotalGames() + 1);
        profile.setTotalQuestions(profile.getTotalQuestions() + questions.size());
        profile.setTotalCorrect(profile.getTotalCorrect() + Math.max(playerOneScore , playerTwoScore));
        profile.setPvpGames(profile.getPvpGames() + 1);

        ProfileManager.saveProfile(profile);
    }
}