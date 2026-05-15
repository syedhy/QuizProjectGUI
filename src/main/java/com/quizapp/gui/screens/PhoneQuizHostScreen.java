package com.quizapp.gui.screens;

import java.util.List;

import com.quizapp.gui.NavigationManager;
import com.quizapp.gui.components.GlassCard;
import com.quizapp.gui.effects.AnimatedBackground;
import com.quizapp.helpers.Question;
import com.quizapp.network.PhoneQuizServer;
import com.quizapp.network.PhoneQuizState;
import com.quizapp.network.QRCodeGenerator;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class PhoneQuizHostScreen extends StackPane {

    private static final int PORT = 6969;

    private PhoneQuizServer server;
    private Timeline updater;

    private int lastSeenAnswerVersion = 0;
    private long lastFeedbackShownAt = 0;

    public PhoneQuizHostScreen(List<Question> questions) {
        AnimatedBackground bg = new AnimatedBackground();

        VBox root = new VBox(26);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        GlassCard card = new GlassCard();

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);

        Label title = new Label("Scan To Play");
        title.getStyleClass().add("setup-main-title");

        Label subtitle = new Label("Open this quiz on your phone");
        subtitle.getStyleClass().add("setup-subtitle");

        Label urlLabel = new Label();
        urlLabel.getStyleClass().add("setup-section-subtitle");

        Label statusLabel = new Label("Waiting for phone...");
        statusLabel.getStyleClass().add("setup-info-box");

        Label feedbackLabel = new Label("Answer feedback will appear here");
        feedbackLabel.getStyleClass().add("phone-feedback-neutral");

        ImageView qrView = new ImageView();
        qrView.setFitWidth(320);
        qrView.setFitHeight(320);

        Button stopButton = new Button("Stop And Go Back");
        stopButton.getStyleClass().add("setup-back-button");

        try {
            server = new PhoneQuizServer(questions);
            server.start(PORT);

            System.out.println("Phone quiz server started on port : " + PORT);

            String url = server.getServerUrl(PORT);
            urlLabel.setText(url);
            qrView.setImage(QRCodeGenerator.generate(url , 320));

        } catch (Exception ex) {
            statusLabel.setText("Could not start phone server : " + ex.getMessage());
        }

        stopButton.setOnAction(e -> {
            stopServer();
            NavigationManager.goTo(new ModeSelectScreen());
        });

        content.getChildren().addAll(
                title,
                subtitle,
                qrView,
                urlLabel,
                statusLabel,
                feedbackLabel,
                stopButton
        );

        card.getChildren().add(content);
        root.getChildren().add(card);

        getChildren().addAll(bg , root);

        updater = new Timeline(new KeyFrame(Duration.millis(250) , e -> {
            if (server == null) {
                return;
            }

            PhoneQuizState state = server.getState();

            updateFeedbackLabel(state , feedbackLabel);

            if (state.isFinished()) {
                statusLabel.setText(
                        "Quiz Complete  |  Score : "
                                + state.getCorrectAnswers()
                                + " / "
                                + state.getTotalQuestions()
                                + "  |  Wrong : "
                                + state.getWrongAnswers()
                );

                updater.stop();

                NavigationManager.goTo(
                        new PhoneQuizResultScreen(
                                state.getCorrectAnswers(),
                                state.getWrongAnswers(),
                                state.getTotalQuestions()
                        )
                );

            } else {
                statusLabel.setText(
                        "Live Progress : Question "
                                + (state.getCurrentIndex() + 1)
                                + " / "
                                + state.getTotalQuestions()
                                + "  |  Correct : "
                                + state.getCorrectAnswers()
                                + "  |  Wrong : "
                                + state.getWrongAnswers()
                );
            }
        }));

        updater.setCycleCount(Timeline.INDEFINITE);
        updater.play();
    }

    private void updateFeedbackLabel(PhoneQuizState state , Label feedbackLabel) {
        if (!state.hasLastAnswer()) {
            setNeutralFeedback(feedbackLabel);
            return;
        }

        if (state.getAnswerVersion() != lastSeenAnswerVersion) {
            lastSeenAnswerVersion = state.getAnswerVersion();
            lastFeedbackShownAt = System.currentTimeMillis();
        }

        long now = System.currentTimeMillis();

        feedbackLabel.getStyleClass().removeAll(
                "phone-feedback-neutral",
                "phone-feedback-correct",
                "phone-feedback-wrong"
        );

        if (now - lastFeedbackShownAt <= 1500) {
            if (state.wasLastAnswerCorrect()) {
                feedbackLabel.setText("✓ Correct");
                feedbackLabel.getStyleClass().add("phone-feedback-correct");
            } else {
                feedbackLabel.setText("✕ Wrong");
                feedbackLabel.getStyleClass().add("phone-feedback-wrong");
            }
        } else {
            setNeutralFeedback(feedbackLabel);
        }
    }

    private void setNeutralFeedback(Label feedbackLabel) {
        feedbackLabel.getStyleClass().removeAll(
                "phone-feedback-correct",
                "phone-feedback-wrong"
        );

        if (!feedbackLabel.getStyleClass().contains("phone-feedback-neutral")) {
            feedbackLabel.getStyleClass().add("phone-feedback-neutral");
        }

        feedbackLabel.setText("Answer feedback will appear here");
    }

    private void stopServer() {
        if (updater != null) {
            updater.stop();
        }

        if (server != null) {
            server.stop();
        }
    }
}