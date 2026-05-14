package com.quizapp.gui.screens;

import com.quizapp.gui.NavigationManager;
import com.quizapp.gui.components.GlassCard;
import com.quizapp.gui.effects.AnimatedBackground;
import com.quizapp.profiles.Profile;
import com.quizapp.profiles.ProfileSession;
import com.quizapp.gui.effects.UIAnimations;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class HomeScreen extends StackPane {

    public HomeScreen() {
        Profile profile = ProfileSession.getCurrentProfile();

        AnimatedBackground bg = new AnimatedBackground();

        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));

        Label title = new Label("QUIZ PROJECT");
        title.getStyleClass().add("home-title");

        Label subtitle = new Label("Modern Competitive Quiz Experience");
        subtitle.getStyleClass().add("home-subtitle");

        Label profileLabel = new Label("Playing as : " + profile.getName() + "     ELO : " + profile.getElo());
        profileLabel.getStyleClass().add("home-profile-label");

        GlassCard card = new GlassCard();

        VBox cardContent = new VBox(24);
        cardContent.setAlignment(Pos.CENTER);

        Button startButton = new Button("Start Playing");
        startButton.getStyleClass().add("home-start-button");
        startButton.setOnAction(e -> { 
            UIAnimations.buttonPress(startButton);
            NavigationManager.goTo(new ModeSelectScreen());});

        GridPane actionGrid = new GridPane();
        actionGrid.setHgap(18);
        actionGrid.setVgap(18);
        actionGrid.setAlignment(Pos.CENTER);

        Button dashboardButton = new Button("Dashboard");
        dashboardButton.getStyleClass().add("home-action-button");
        dashboardButton.setOnAction(e -> NavigationManager.goTo(new DashboardScreen()));

        Button switchProfileButton = new Button("Switch Profile");
        switchProfileButton.getStyleClass().add("home-action-button");
        switchProfileButton.setOnAction(e -> NavigationManager.goTo(new ProfileSelectScreen()));

        actionGrid.add(dashboardButton , 0 , 0);
        actionGrid.add(switchProfileButton , 1 , 0);

        cardContent.getChildren().addAll(startButton , actionGrid);
        UIAnimations.slideFadeIn(root); 
        card.getChildren().add(cardContent);

        Button settingsButton = new Button("⚙");
        settingsButton.getStyleClass().add("settings-icon-button");
        settingsButton.setOnAction(e -> NavigationManager.goTo(new SettingsScreen()));

        StackPane.setAlignment(settingsButton , Pos.BOTTOM_RIGHT);
        StackPane.setMargin(settingsButton , new Insets(0 , 34 , 30 , 0));

        root.getChildren().addAll(title , subtitle , profileLabel , card);

        getChildren().addAll(bg , root , settingsButton);
    }
}