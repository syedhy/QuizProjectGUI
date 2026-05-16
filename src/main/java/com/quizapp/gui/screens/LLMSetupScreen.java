package com.quizapp.gui.screens;

import java.util.List;

import com.quizapp.gui.NavigationManager;
import com.quizapp.gui.QuizModeConfig;
import com.quizapp.gui.components.GlassCard;
import com.quizapp.gui.components.LoadingOverlay;
import com.quizapp.gui.effects.AnimatedBackground;
import com.quizapp.helpers.Question;
import com.quizapp.modes.llmmode.api.GeminiClient;
import com.quizapp.modes.llmmode.api.GemmaClient;
import com.quizapp.modes.llmmode.helpers.QuizParser;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LLMSetupScreen extends StackPane {

    private final Label errorLabel = new Label();

    public LLMSetupScreen() {
        AnimatedBackground bg = new AnimatedBackground();

        VBox root = new VBox(28);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        GlassCard card = new GlassCard();

        VBox content = new VBox(24);
        content.setAlignment(Pos.CENTER);

        Label title = new Label("AI Quiz Generator");
        title.getStyleClass().add("setup-main-title");

        Label subtitle = new Label("Generate custom questions from any topic");
        subtitle.getStyleClass().add("setup-subtitle");

        Label topicTitle = new Label("ENTER TOPIC");
        topicTitle.getStyleClass().add("setup-section-title");

        TextField topicField = new TextField();
        topicField.setPromptText("Example  :  Java  , World History , Cartoons");
        topicField.getStyleClass().add("llm-topic-input");
        topicField.setMaxWidth(760);

        Label difficultyTitle = new Label("CHOOSE DIFFICULTY");
        difficultyTitle.getStyleClass().add("setup-section-title");

        TextField customDifficultyField = new TextField();
        customDifficultyField.setPromptText("Example  :  College exam level , Tricky , Beginner friendly");
        customDifficultyField.getStyleClass().add("llm-topic-input");
        customDifficultyField.setMaxWidth(760);

        Label engineTitle = new Label("CHOOSE AI ENGINE");
        engineTitle.getStyleClass().add("setup-section-title");

        HBox engineRow = new HBox(20);
        engineRow.setAlignment(Pos.CENTER);

        Button geminiButton = createSelectButton("Gemini");
        Button gemmaButton = createSelectButton("Gemma");

        engineRow.getChildren().addAll(geminiButton , gemmaButton);

        final String[] selectedEngine = {"Gemini"};

        setSelected(geminiButton);

        geminiButton.setOnAction(e -> {
            selectedEngine[0] = "Gemini";
            clearSelection(geminiButton , gemmaButton);
            setSelected(geminiButton);
        });

        gemmaButton.setOnAction(e -> {
            selectedEngine[0] = "Gemma";
            clearSelection(geminiButton , gemmaButton);
            setSelected(gemmaButton);
        });

        errorLabel.getStyleClass().add("error-label");

        Label info = new Label("Gemini uses cloud generation. Gemma uses your local Ollama model");
        info.getStyleClass().add("setup-info-box");
        info.setWrapText(true);
        info.setMaxWidth(760);

        HBox actionRow = new HBox(18);
        actionRow.setAlignment(Pos.CENTER);

        Button backButton = new Button("Back");
        backButton.getStyleClass().add("setup-back-button");
        backButton.setOnAction(e -> NavigationManager.goTo(new ModeSelectScreen()));

        Button generateButton = new Button("Generate Quiz");
        generateButton.getStyleClass().add("setup-start-button");

        LoadingOverlay overlay = new LoadingOverlay("Generating AI Questions...");

        generateButton.setOnAction(e -> {
            String topic = topicField.getText();

            if (topic == null || topic.isBlank()) {
                errorLabel.setText("Enter a topic first");
                return;
            }

            String difficultyText = customDifficultyField.getText();

            if (difficultyText == null || difficultyText.isBlank()) {
                errorLabel.setText("Enter a difficulty first");
                return;
            }

            final String difficultyForAI = difficultyText.trim();
            errorLabel.setText("");
            overlay.show();
            overlay.setMessage("Generating " + difficultyForAI + " questions on " + topic.trim() + "...");
            generateButton.setDisable(true);

            Task<List<Question>> task = new Task<>() {
                @Override
                protected List<Question> call() throws Exception {
                    String raw;

                    if (selectedEngine[0].equalsIgnoreCase("Gemini")) {
                        raw = GeminiClient.generateQuiz(topic.trim() , difficultyForAI);
                    } else {
                        raw = GemmaClient.generateQuiz(topic.trim() , difficultyForAI);
                    }

                    return QuizParser.parseQuestions(raw);
                }
            };

            task.setOnSucceeded(ev -> {
                overlay.hide();
                generateButton.setDisable(false);

                List<Question> questions = task.getValue();

                if (questions == null || questions.isEmpty()) {
                    errorLabel.setText("AI did not generate valid questions");
                    return;
                }

                NavigationManager.goTo(new QuizScreen(QuizModeConfig.llm() , questions));
            });

            task.setOnFailed(ev -> {
                overlay.hide();
                generateButton.setDisable(false);

                Throwable error = task.getException();

                if (error == null || error.getMessage() == null) {
                    errorLabel.setText("AI generation failed");
                } else {
                    errorLabel.setText(error.getMessage());
                }
            });

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        });

        actionRow.getChildren().addAll(backButton , generateButton);

        content.getChildren().addAll(
                title,
                subtitle,
                topicTitle,
                topicField,
                difficultyTitle,
                customDifficultyField,
                engineTitle,
                engineRow,
                info,
                errorLabel,
                actionRow
        );

        card.getChildren().add(content);
        root.getChildren().add(card);

        getChildren().addAll(bg , root , overlay);
    }

    private Button createSelectButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("setup-pill-button");
        button.setMinWidth(220);
        button.setMinHeight(70);
        return button;
    }

    private void clearSelection(Button... buttons) {
        for (Button button : buttons) {
            button.getStyleClass().remove("setup-pill-selected");
        }
    }

    private void setSelected(Button button) {
        if (!button.getStyleClass().contains("setup-pill-selected")) {
            button.getStyleClass().add("setup-pill-selected");
        }
    }
}