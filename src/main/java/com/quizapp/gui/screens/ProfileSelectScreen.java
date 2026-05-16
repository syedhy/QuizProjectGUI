package com.quizapp.gui.screens;

import java.util.List;

import com.quizapp.gui.NavigationManager;
import com.quizapp.gui.effects.AnimatedBackground;
import com.quizapp.profiles.Profile;
import com.quizapp.profiles.ProfileManager;
import com.quizapp.profiles.ProfileSession;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class ProfileSelectScreen extends StackPane {

    private final FlowPane profileGrid = new FlowPane();
    private final Label messageLabel = new Label();

    public ProfileSelectScreen() {
        AnimatedBackground bg = new AnimatedBackground();

        VBox root = new VBox(30);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(42));

        VBox hero = createHero();
        HBox mainLayout = new HBox(28);
        mainLayout.setAlignment(Pos.CENTER);

        VBox savedProfilesPanel = createSavedProfilesPanel();
        VBox actionPanel = createActionPanel();

        mainLayout.getChildren().addAll(savedProfilesPanel , actionPanel);

        root.getChildren().addAll(hero , mainLayout);

        getChildren().addAll(bg , root);

        fadeIn(root);
    }

    private VBox createHero() {
        VBox hero = new VBox(10);
        hero.setAlignment(Pos.CENTER);

        Label badge = new Label("QUIZ PROJECT");
        badge.getStyleClass().add("profile-badge");

        Label title = new Label("Choose Your Player");
        title.getStyleClass().add("profile-main-title");

        Label subtitle = new Label("Select a saved profile , create a new one , or continue as guest");
        subtitle.getStyleClass().add("profile-main-subtitle");

        hero.getChildren().addAll(badge , title , subtitle);

        return hero;
    }

    private VBox createSavedProfilesPanel() {
        VBox panel = new VBox(20);
        panel.getStyleClass().add("profile-main-panel");
        panel.setPadding(new Insets(30));
        panel.setPrefWidth(860);
        panel.setPrefHeight(600);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox headerText = new VBox(4);

        Label title = new Label("Saved Profiles");
        title.getStyleClass().add("profile-panel-title");

        Label subtitle = new Label("Continue from where you left off");
        subtitle.getStyleClass().add("profile-panel-subtitle");

        headerText.getChildren().addAll(title , subtitle);
        header.getChildren().add(headerText);

        profileGrid.setHgap(20);
        profileGrid.setVgap(24);
        profileGrid.setPadding(new Insets(15));
        profileGrid.setAlignment(Pos.TOP_LEFT);
        profileGrid.setPrefWrapLength(800);
        profileGrid.setMinWidth(800);
        profileGrid.setPrefWidth(800);

        loadProfiles();

        ScrollPane scrollPane = new ScrollPane(profileGrid);
        scrollPane.getStyleClass().add("profile-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPrefHeight(470);
        scrollPane.setPadding(new Insets(0));

        panel.getChildren().addAll(header , scrollPane);

        return panel;
    }

    private VBox createActionPanel() {
        VBox panel = new VBox(24);
        panel.getStyleClass().add("profile-side-panel");
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setPadding(new Insets(30));
        panel.setPrefWidth(420);
        panel.setPrefHeight(560);

        Label createTitle = new Label("Create New Profile");
        createTitle.getStyleClass().add("profile-panel-title");

        Label createSubtitle = new Label("Save your ELO , stats , accuracy and game history");
        createSubtitle.getStyleClass().add("profile-panel-subtitle");
        createSubtitle.setWrapText(true);

        TextField nameField = new TextField();
        nameField.setPromptText("Enter player name");
        nameField.getStyleClass().add("profile-big-input");
        nameField.setMaxWidth(Double.MAX_VALUE);

        Button createButton = new Button("Create Profile");
        createButton.getStyleClass().add("profile-primary-action");
        createButton.setMaxWidth(Double.MAX_VALUE);

        createButton.setOnAction(e -> {
            String name = nameField.getText();

            if (name == null || name.isBlank()) {
                messageLabel.setText("Enter a profile name");
                return;
            }

            ProfileManager.createProfile(name.trim());
            Profile profile = ProfileManager.getProfile(name.trim());

            if (profile == null) {
                messageLabel.setText("Could not create profile");
                return;
            }

            ProfileSession.setCurrentProfile(profile);
            NavigationManager.goTo(new HomeScreen());
        });

        Label divider = new Label("or");
        divider.getStyleClass().add("profile-divider-text");

        Button guestButton = new Button("Play As Guest");
        guestButton.getStyleClass().add("profile-guest-action");
        guestButton.setMaxWidth(Double.MAX_VALUE);

        guestButton.setOnAction(e -> {
            Profile guest = new Profile("Guest");
            guest.setElo(1000);
            ProfileSession.setCurrentProfile(guest);
            NavigationManager.goTo(new HomeScreen());
        });

        messageLabel.getStyleClass().add("error-label");

        panel.getChildren().addAll(
                createTitle,
                createSubtitle,
                nameField,
                createButton,
                divider,
                guestButton,
                messageLabel
        );

        return panel;
    }

    private void loadProfiles() {
        profileGrid.getChildren().clear();

        List<Profile> profiles = ProfileManager.getAllProfiles();

        if (profiles.isEmpty()) {
            VBox empty = new VBox(12);
            empty.setAlignment(Pos.CENTER);
            empty.setPrefWidth(680);
            empty.setPrefHeight(300);

            Label title = new Label("No Profiles Yet");
            title.getStyleClass().add("profile-empty-title");

            Label subtitle = new Label("Create your first profile to start saving progress");
            subtitle.getStyleClass().add("profile-empty-subtitle");

            empty.getChildren().addAll(title , subtitle);
            profileGrid.getChildren().add(empty);
            return;
        }

        for (Profile profile : profiles) {
            VBox tile = createProfileTile(profile);
            profileGrid.getChildren().add(tile);
        }
    }

    private VBox createProfileTile(Profile profile) {
        VBox tile = new VBox(12);
        tile.getStyleClass().add("profile-grid-tile");
        tile.setPrefWidth(360);
        tile.setMinWidth(360);
        tile.setMaxWidth(360);
        tile.setPrefHeight(178);
        tile.setAlignment(Pos.CENTER_LEFT);
        tile.setPadding(new Insets(22));

        Label name = new Label(profile.getName());
        name.getStyleClass().add("profile-tile-name");

        Label elo = new Label("ELO : " + profile.getElo());
        elo.getStyleClass().add("profile-tile-meta");

        Label stats = new Label(
                "Games : " + profile.getTotalGames()
                        + "     Accuracy : "
                        + String.format("%.1f %%", profile.getOverallAccuracy()));

        stats.getStyleClass().add("profile-tile-meta");
        stats.setWrapText(false);
        stats.setMinWidth(320);
        tile.setOnMouseClicked(e -> {
            ProfileSession.setCurrentProfile(profile);
            NavigationManager.goTo(new HomeScreen());
        });

        tile.getChildren().addAll(name , elo , stats);

        return tile;
    }

    private void fadeIn(VBox root) {
        root.setOpacity(0);

        FadeTransition fade = new FadeTransition(Duration.millis(500) , root);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }
}