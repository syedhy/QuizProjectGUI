package com.quizapp.gui.screens;

import java.util.Collections;
import java.util.List;
import com.quizapp.gui.NavigationManager;
import com.quizapp.gui.QuizModeConfig;
import com.quizapp.gui.components.GlassCard;
import com.quizapp.gui.effects.AnimatedBackground;
import com.quizapp.helpers.ListMaker;
import com.quizapp.helpers.Question;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class QuizSetupScreen extends StackPane {

    public QuizSetupScreen(QuizModeConfig config) {
        AnimatedBackground bg = new AnimatedBackground();

        VBox root = new VBox(26);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(42));

        GlassCard card = new GlassCard();
        card.setMaxWidth(980);

        VBox content = new VBox(30);
        content.setAlignment(Pos.CENTER);

        Label modeTitle = new Label(config.getModeName());
        modeTitle.getStyleClass().add("setup-screen-title");

        Label modeSubtitle = new Label("Customize your quiz experience");
        modeSubtitle.getStyleClass().add("setup-screen-subtitle");

        Label subjectTitle = new Label("📚  SUBJECT");
        subjectTitle.getStyleClass().add("setup-section-heading");

        Label subjectSub = new Label("Choose your quiz category");
        subjectSub.getStyleClass().add("setup-section-subtext");

        HBox subjectRow = new HBox(18);
        subjectRow.setAlignment(Pos.CENTER);

        Button gkButton = createSetupButton("General Knowledge" , 300 , 76);
        Button mathButton = createSetupButton("Math" , 190 , 76);
        Button scienceButton = createSetupButton("Science" , 210 , 76);

        subjectRow.getChildren().addAll(gkButton , mathButton , scienceButton);

        Label difficultyTitle = new Label("⚡  DIFFICULTY");
        difficultyTitle.getStyleClass().add("setup-section-heading");

        Label difficultySub = new Label("Choose your challenge level");
        difficultySub.getStyleClass().add("setup-section-subtext");

        HBox difficultyRow = new HBox(18);
        difficultyRow.setAlignment(Pos.CENTER);

        Button easyButton = createSetupButton("Easy" , 190 , 76);
        Button mediumButton = createSetupButton("Medium" , 210 , 76);
        Button hardButton = createSetupButton("Hard" , 190 , 76);

        difficultyRow.getChildren().addAll(easyButton , mediumButton , hardButton);

        Label info = new Label(getModeInfo(config));
        info.getStyleClass().add("setup-mode-info");
        info.setWrapText(true);
        info.setMaxWidth(760);

        HBox actions = new HBox(18);
        actions.setAlignment(Pos.CENTER);

        Button backButton = new Button("←  Back");
        backButton.getStyleClass().add("setup-secondary-action");
        backButton.setOnAction(e -> NavigationManager.goTo(new ModeSelectScreen()));

        Button startButton = new Button("▶  Start Quiz");
        startButton.getStyleClass().add("setup-primary-action");

        actions.getChildren().addAll(backButton , startButton);

        final String[] selectedSubject = {"General Knowledge"};
        final String[] selectedDifficulty = {"Easy"};

        setSelected(gkButton);
        setSelected(easyButton);

        gkButton.setOnAction(e -> {
            selectedSubject[0] = "General Knowledge";
            clearSelection(gkButton , mathButton , scienceButton);
            setSelected(gkButton);
        });

        mathButton.setOnAction(e -> {
            selectedSubject[0] = "Math";
            clearSelection(gkButton , mathButton , scienceButton);
            setSelected(mathButton);
        });

        scienceButton.setOnAction(e -> {
            selectedSubject[0] = "Science";
            clearSelection(gkButton , mathButton , scienceButton);
            setSelected(scienceButton);
        });

        easyButton.setOnAction(e -> {
            selectedDifficulty[0] = "Easy";
            clearSelection(easyButton , mediumButton , hardButton);
            setSelected(easyButton);
        });

        mediumButton.setOnAction(e -> {
            selectedDifficulty[0] = "Medium";
            clearSelection(easyButton , mediumButton , hardButton);
            setSelected(mediumButton);
        });

        hardButton.setOnAction(e -> {
            selectedDifficulty[0] = "Hard";
            clearSelection(easyButton , mediumButton , hardButton);
            setSelected(hardButton);
        });

        startButton.setOnAction(e -> {
            String path = buildPath(selectedSubject[0], selectedDifficulty[0]);
            List<Question> questions = ListMaker.makeList(path);

            if (config.getModeName().equals("Player Vs Player")) {
                NavigationManager.goTo(new PvpQuizScreen(config, questions));
            } else if (config.getModeName().equals("Phone QR Mode")) {
                Collections.shuffle(questions);

                if (questions.size() > config.getQuestionLimit()) {
                    questions = questions.subList(0, config.getQuestionLimit());
                }

                NavigationManager.goTo(new PhoneQuizHostScreen(questions));
            } else {
                NavigationManager.goTo(new QuizScreen(config, questions));
            }
        });

        content.getChildren().addAll(
                modeTitle,
                modeSubtitle,
                subjectTitle,
                subjectSub,
                subjectRow,
                difficultyTitle,
                difficultySub,
                difficultyRow,
                info,
                actions
        );

        card.getChildren().add(content);
        root.getChildren().add(card);

        getChildren().addAll(bg , root);
    }

    private Button createSetupButton(String text , int width , int height) {
        Button button = new Button(text);

        button.getStyleClass().add("setup-pill-button");
        button.setMinWidth(width);
        button.setMinHeight(height);

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

    private String buildPath(String subject , String difficulty) {
        String folder;

        switch (subject) {
            case "Science":
                folder = "Science";
                break;

            case "Math":
                folder = "Math";
                break;

            default:
                folder = "GeneralKnowledge";
        }

        return "Data/" + folder + "/" + difficulty.toLowerCase() + ".txt";
    }

    private String getModeInfo(QuizModeConfig config) {
        if (config.getModeName().equals("Timed Mode")) return "You have limited time to answer each question. Answer quickly and accurately to maximize your score";
        if (config.getModeName().equals("Survival Mode")) return "You only have 3 lives. Each wrong answer costs one life";
        if (config.getModeName().equals("Sudden Death")) return "One wrong answer ends the quiz immediately. Choose carefully";
        if (config.getModeName().equals("Player Vs Player")) return "Two players take turns answering questions. The higher score wins";
        if (config.getModeName().equals("ELO Mode")) return "Your ranked performance affects your ELO rating after the match";
        if (config.getModeName().equals("Phone QR Mode")) return "Scan the QR code on your phone and answer the quiz there. Results will appear on both devices";

        return "Choose your setup and start the challenge";
    }
}