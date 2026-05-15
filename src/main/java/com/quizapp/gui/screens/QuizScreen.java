package com.quizapp.gui.screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.quizapp.core.QuizSession;
import com.quizapp.gui.AppSettings;
import com.quizapp.gui.NavigationManager;
import com.quizapp.gui.QuizModeConfig;
import com.quizapp.gui.QuizResultData;
import com.quizapp.gui.components.GlassCard;
import com.quizapp.gui.effects.AnimatedBackground;
import com.quizapp.helpers.Question;
import com.quizapp.profiles.Profile;
import com.quizapp.profiles.ProfileManager;
import com.quizapp.profiles.ProfileSession;

import javafx.scene.input.KeyCode;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class QuizScreen extends StackPane {

    private int selectedOptionIndex = 0;
    private int attemptedQuestions;
    private int wrongAnswers;
    private final QuizModeConfig config;
    private final QuizSession session;

    private final Label progressLabel = new Label();
    private final Label scoreLabel = new Label();
    private final Label statusLabel = new Label();
    private final Label modeLabel = new Label();
    private final Label questionLabel = new Label();

    private final VBox optionsBox = new VBox(18);
    private final ProgressBar progressBar = new ProgressBar();

    private GlassCard questionCard;

    private int livesRemaining;
    private int timeRemaining;
    private Timeline timer;
    private Timeline progressAnimation;
    private boolean quizFinished;

    public QuizScreen(QuizModeConfig config , List<Question> questions) {
        this.config = config;
        this.session = new QuizSession(limitQuestions(questions , config.getQuestionLimit()));
        this.livesRemaining = config.getLives();
        this.timeRemaining = config.getTimeLimitSeconds();
        this.quizFinished = false;
        this.attemptedQuestions = 0;
        this.wrongAnswers = 0;

        AnimatedBackground bg = new AnimatedBackground();

        VBox root = new VBox(20);
        root.setPadding(new Insets(28));
        root.setAlignment(Pos.TOP_CENTER);

        GlassCard hudCard = new GlassCard();
        hudCard.setMaxWidth(1100);

        VBox hudContent = new VBox(18);
        hudContent.setAlignment(Pos.CENTER);

        modeLabel.setText(config.getModeName());
        modeLabel.getStyleClass().add("quiz-mode-title");

        HBox statsRow = new HBox(28);
        statsRow.setAlignment(Pos.CENTER);

        progressLabel.getStyleClass().add("quiz-hud-text");
        scoreLabel.getStyleClass().add("quiz-hud-text");
        statusLabel.getStyleClass().add("quiz-hud-text");

        statsRow.getChildren().addAll(progressLabel , scoreLabel , statusLabel);

        progressBar.getStyleClass().add("quiz-progress-modern");
        progressBar.setPrefWidth(860);
        progressBar.setPrefHeight(30);

        hudContent.getChildren().addAll(modeLabel , statsRow , progressBar);
        hudCard.getChildren().add(hudContent);

        questionCard = new GlassCard();
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

        Button exitButton = new Button("Exit Quiz");
        exitButton.getStyleClass().add("secondary-button");

        exitButton.setOnAction(e -> {
            stopTimer();
            NavigationManager.goTo(new ModeSelectScreen());
        });

        root.getChildren().addAll(hudCard , questionCard , exitButton);
        getChildren().addAll(bg , root);

        Platform.runLater(this::requestFocus);        

        if (session.getTotalQuestions() == 0) {
            progressLabel.setText("No questions loaded");
            questionLabel.setText("Question file was not found or is empty");
            return;
        }

        if (config.hasTimer()) startTimer();

        renderQuestion();
    }

    private static List<Question> limitQuestions(List<Question> questions , int limit) {
        if (questions == null || questions.isEmpty()) return new ArrayList<>();

        List<Question> copy = new ArrayList<>(questions);
        Collections.shuffle(copy);

        return new ArrayList<>(copy.subList(0 , Math.min(limit , copy.size())));
    }

    private void renderQuestion() {
        Question q = session.getCurrentQuestion();

        progressLabel.setText("Question " + (session.getCurrentIndex() + 1) + " / " + session.getTotalQuestions());
        scoreLabel.setText("Score : " + session.getCorrectAnswers());
        updateStatusLabel();

        animateProgress((double) (session.getCurrentIndex() + 1) / session.getTotalQuestions());

        questionLabel.setText(q.question);
        optionsBox.getChildren().clear();

        for (int i = 0; i < q.options.length; i++) {
            String optionLetter = String.valueOf((char) ('A' + i));
            String optionText = q.options[i];

            Button button = new Button(optionLetter + ". " + optionText);
            button.getStyleClass().add("quiz-option-button");
            button.setMinHeight(82);
            button.setMaxWidth(Double.MAX_VALUE);
            button.setWrapText(true);
            button.setAlignment(Pos.CENTER_LEFT);

            final int optionIndex = i;

            button.setOnAction(e -> handleAnswer(button, optionLetter));

            button.setOnMouseEntered(e -> {
                selectedOptionIndex = optionIndex;
                highlightSelectedOption();
                requestFocus();
    });

            button.setOnMouseClicked(e -> {
                selectedOptionIndex = optionIndex;
                highlightSelectedOption();
                requestFocus();
            });

            optionsBox.getChildren().add(button);
        }

        selectedOptionIndex = 0;
        highlightSelectedOption();
        setupKeyboardControls();
        animateQuestionIn();
    }

    private void setupKeyboardControls() {
        setFocusTraversable(true);

        Platform.runLater(() -> {
            requestFocus();
        });

        setOnMouseClicked(e -> requestFocus());

        setOnKeyPressed(event -> {
            if (optionsBox.getChildren().isEmpty())
                return;

            if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.RIGHT) {
                selectedOptionIndex++;

                if (selectedOptionIndex >= optionsBox.getChildren().size()) {
                    selectedOptionIndex = 0;
                }

                highlightSelectedOption();
                event.consume();
            }

            if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.LEFT) {
                selectedOptionIndex--;

                if (selectedOptionIndex < 0) {
                    selectedOptionIndex = optionsBox.getChildren().size() - 1;
                }

                highlightSelectedOption();
                event.consume();
            }

            if (event.getCode() == KeyCode.ENTER) {
                Button selectedButton = (Button) optionsBox.getChildren().get(selectedOptionIndex);

                if (!selectedButton.isDisabled()) {
                    selectedButton.fire();
                }

            event.consume();
        }
    });
}

private void highlightSelectedOption() {
    for (int i = 0; i < optionsBox.getChildren().size(); i++) {
        Button button = (Button) optionsBox.getChildren().get(i);

        button.getStyleClass().remove("keyboard-selected-option");

        if (i == selectedOptionIndex && !button.isDisabled()) {
            button.getStyleClass().add("keyboard-selected-option");
        }
    }
}

    private void animateProgress(double targetValue) {
        if (!AppSettings.isAnimationsEnabled()) {
            progressBar.setProgress(targetValue);
            return;
        }

        if (progressAnimation != null) progressAnimation.stop();

        progressAnimation = new Timeline(
                new KeyFrame(
                        Duration.millis(420),
                        new KeyValue(progressBar.progressProperty() , targetValue)
                )
        );

        progressAnimation.play();
    }

    private void animateQuestionIn() {
        if (!AppSettings.isAnimationsEnabled()) return;

        questionCard.setOpacity(0);
        questionCard.setTranslateY(18);

        FadeTransition fade = new FadeTransition(Duration.millis(320) , questionCard);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(320) , questionCard);
        slide.setFromY(18);
        slide.setToY(0);

        fade.play();
        slide.play();
    }

    private void handleAnswer(Button clickedButton , String selectedLetter) {
        Question q = session.getCurrentQuestion();

        String correctLetter = q.answer.trim().toUpperCase();
        boolean correct = correctLetter.equalsIgnoreCase(selectedLetter);

        if (correct) {
            com.quizapp.gui.SoundManager.playCorrect();
        } else {
            com.quizapp.gui.SoundManager.playWrong();
        }
        
        attemptedQuestions++;

        if (!correct) {
            wrongAnswers++;
        }

        if (correct) {
            session.submitAnswer(correctLetter);
        } else if (config.hasLives()) {
            livesRemaining--;
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

        if (!correct && config.isSuddenDeath()) {
            delayThenFinish();
            return;
        }

        if (!correct && config.hasLives() && livesRemaining <= 0) {
            delayThenFinish();
            return;
        }

        delayThenNext();
    }

    private void delayThenNext() {
        new Thread(() -> {
            sleep();

            Platform.runLater(() -> {
                if (session.hasNextQuestion()) {
                    session.nextQuestion();
                    renderQuestion();
                } else {
                    finishQuiz();
                }
            });
        }).start();
    }

    private void delayThenFinish() {
        new Thread(() -> {
            sleep();
            Platform.runLater(this::finishQuiz);
        }).start();
    }

    private void sleep() {
        try {
            Thread.sleep(900);
        } catch (Exception ignored) {
        }
    }

    private void updateStatusLabel() {
        String text = "";

        if (config.hasTimer()) text += "Time : " + timeRemaining + "s";
        if (config.hasLives()) text += (text.isEmpty() ? "" : "     ") + "Lives : " + livesRemaining;
        if (config.isRanked()) text += (text.isEmpty() ? "" : "     ") + "Ranked Match";

        statusLabel.setText(text);
    }

    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1) , e -> {
            timeRemaining--;
            updateStatusLabel();

            if (timeRemaining <= 0) finishQuiz();
        }));

        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void stopTimer() {
        if (timer != null) timer.stop();
    }

    private void finishQuiz() {
        if (quizFinished)
            return;

        quizFinished = true;
        stopTimer();

        int correct = session.getCorrectAnswers();
        int total = attemptedQuestions;
        int wrong = wrongAnswers;

        if (total == 0) {
            total = session.getTotalQuestions();
            wrong = total - correct;
        }

        int[] eloData = saveProfileStats(correct, total);

    QuizResultData result = new QuizResultData(
            correct,
            total,
            correct,
            wrong,
            eloData[0],
            eloData[1]
    );

    NavigationManager.goTo(new ResultScreen(config , result));
}

    private int[] saveProfileStats(int correct , int total) {
        Profile profile = ProfileSession.getCurrentProfile();

        if (profile == null) return new int[]{-1 , -1};

        int oldElo = profile.getElo();
        int newElo = oldElo;

        profile.setTotalGames(profile.getTotalGames() + 1);
        profile.setTotalQuestions(profile.getTotalQuestions() + total);
        profile.setTotalCorrect(profile.getTotalCorrect() + correct);

        switch (config.getModeName()) {
            case "Timed Mode" -> profile.setTimedGames(profile.getTimedGames() + 1);
            case "Survival Mode" -> profile.setSurvivalGames(profile.getSurvivalGames() + 1);
            case "Sudden Death" -> profile.setSuddenDeathGames(profile.getSuddenDeathGames() + 1);
            case "Player Vs Player" -> profile.setPvpGames(profile.getPvpGames() + 1);
            case "LLM Mode" -> profile.setLlmGames(profile.getLlmGames() + 1);
            case "ELO Mode" -> profile.setEloGames(profile.getEloGames() + 1);
        }

        if (config.isRanked()) {
            profile.setRankedGames(profile.getRankedGames() + 1);
            profile.setRankedTotalScore(profile.getRankedTotalScore() + correct);

            if (correct > profile.getRankedBestScore()) profile.setRankedBestScore(correct);

            double actualScore = (double) correct / total;
            newElo = com.quizapp.elo.EloCalculator.calculateNewRating(oldElo , 1000 , actualScore);

            if (actualScore >= 0.5) profile.setRankedWins(profile.getRankedWins() + 1);
            else profile.setRankedLosses(profile.getRankedLosses() + 1);

            profile.setElo(newElo);
        }

        ProfileManager.saveProfile(profile);

        if (config.isRanked()) return new int[]{oldElo , newElo};

        return new int[]{-1 , -1};
    }
}