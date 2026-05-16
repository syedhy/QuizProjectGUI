package com.quizapp.gui.screens;

import com.quizapp.gui.AppSettings;
import com.quizapp.gui.NavigationManager;
import com.quizapp.gui.components.GlassCard;
import com.quizapp.gui.effects.AnimatedBackground;
import com.quizapp.profiles.Profile;
import com.quizapp.profiles.ProfileManager;
import com.quizapp.profiles.ProfileSession;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SettingsScreen extends StackPane {

    public SettingsScreen() {
        Profile profile = ProfileSession.getCurrentProfile();

        AnimatedBackground bg = new AnimatedBackground();

        VBox root = new VBox(24);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(44));

        GlassCard card = new GlassCard();
        card.setMaxWidth(760);

        VBox content = new VBox(22);
        content.setAlignment(Pos.CENTER);

        Label title = new Label("Settings");
        title.getStyleClass().add("setup-main-title");

        Label subtitle = new Label("Manage your profile and app preferences");
        subtitle.getStyleClass().add("setup-subtitle");

        Label profileInfo = new Label("Current Profile : " + profile.getName() + "     ELO : " + profile.getElo());
        profileInfo.getStyleClass().add("settings-profile-chip");

        CheckBox musicToggle = new CheckBox("Music Enabled");
        musicToggle.getStyleClass().add("settings-toggle");
        musicToggle.setSelected(AppSettings.isMusicEnabled());
        musicToggle.setOnAction(e -> {
            AppSettings.setMusicEnabled(musicToggle.isSelected());

            if (musicToggle.isSelected()) {
                com.quizapp.gui.SoundManager.startMusic();
            } else {
                com.quizapp.gui.SoundManager.stopMusic();
            }
        });

        CheckBox animationToggle = new CheckBox("Animations Enabled");
        animationToggle.getStyleClass().add("settings-toggle");
        animationToggle.setSelected(AppSettings.isAnimationsEnabled());
        animationToggle.setOnAction(e -> AppSettings.setAnimationsEnabled(animationToggle.isSelected()));

        HBox dangerActions = new HBox(14);
        dangerActions.setAlignment(Pos.CENTER);

        Button resetStats = new Button("Reset Stats");
        resetStats.getStyleClass().add("danger-button");
        resetStats.setOnAction(e -> showConfirmation(
                "Reset Profile Stats ?",
                "This will reset ELO , accuracy , games , ranked stats and mode stats for this profile",
                "Reset",
                this::resetCurrentProfile
        ));

        Button deleteProfile = new Button("Delete Profile");
        deleteProfile.getStyleClass().add("danger-button");
        deleteProfile.setOnAction(e -> showConfirmation(
                "Delete Profile ?",
                "This permanently removes the current profile and all saved stats",
                "Delete",
                this::deleteCurrentProfile
        ));

        dangerActions.getChildren().addAll(resetStats , deleteProfile);

        HBox actions = new HBox(14);
        actions.setAlignment(Pos.CENTER);

        Button switchProfile = new Button("Switch Profile");
        switchProfile.getStyleClass().add("secondary-button");
        switchProfile.setOnAction(e -> NavigationManager.goTo(new ProfileSelectScreen()));

        Button back = new Button("Back Home");
        back.getStyleClass().add("primary-button");
        back.setOnAction(e -> NavigationManager.goTo(new HomeScreen()));

        actions.getChildren().addAll(switchProfile , back);

        content.getChildren().addAll(
                title,
                subtitle,
                profileInfo,
                musicToggle,
                animationToggle,
                dangerActions,
                actions
        );

        card.getChildren().add(content);
        root.getChildren().add(card);

        getChildren().addAll(bg , root);
    }

    private void showConfirmation(String titleText , String messageText , String confirmText , Runnable action) {
        StackPane overlay = new StackPane();
        overlay.getStyleClass().add("confirm-overlay");

        VBox popup = new VBox(14);
        popup.getStyleClass().add("confirm-popup");
        popup.setAlignment(Pos.CENTER);
        popup.setPrefWidth(460);
        popup.setMaxWidth(460);
        popup.setPrefHeight(230);
        popup.setMaxHeight(Region.USE_PREF_SIZE);

        Label title = new Label(titleText);
        title.getStyleClass().add("confirm-title");

        Label message = new Label(messageText);
        message.getStyleClass().add("confirm-message");
        message.setWrapText(true);
        message.setMaxWidth(360);

        HBox buttons = new HBox(14);
        buttons.setAlignment(Pos.CENTER);

        Button cancel = new Button("Cancel");
        cancel.getStyleClass().add("secondary-button");
        cancel.setOnAction(e -> getChildren().remove(overlay));

        Button confirm = new Button(confirmText);
        confirm.getStyleClass().add("danger-button");
        confirm.setOnAction(e -> {
            getChildren().remove(overlay);
            action.run();
        });

        buttons.getChildren().addAll(cancel , confirm);
        popup.getChildren().addAll(title , message , buttons);
        StackPane.setAlignment(popup , Pos.CENTER);
        overlay.getChildren().add(popup);
        getChildren().add(overlay);
    }

    private void resetCurrentProfile() {
        Profile oldProfile = ProfileSession.getCurrentProfile();

        if (oldProfile == null || oldProfile.getName().equalsIgnoreCase("Guest")) return;

        Profile freshProfile = new Profile(oldProfile.getName());

        ProfileManager.saveProfile(freshProfile);
        ProfileSession.setCurrentProfile(freshProfile);

        NavigationManager.goTo(new HomeScreen());
    }

    private void deleteCurrentProfile() {
        Profile profile = ProfileSession.getCurrentProfile();

        if (profile == null || profile.getName().equalsIgnoreCase("Guest")) return;

        ProfileManager.deleteProfile(profile.getName());
        ProfileSession.setCurrentProfile(null);

        NavigationManager.goTo(new ProfileSelectScreen());
    }
}