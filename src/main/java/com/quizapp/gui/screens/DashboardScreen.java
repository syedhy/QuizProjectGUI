package com.quizapp.gui.screens;

import com.quizapp.dashboard.DashboardGenerator;
import com.quizapp.gui.NavigationManager;
import com.quizapp.gui.components.StatCard;
import com.quizapp.gui.effects.AnimatedBackground;
import com.quizapp.profiles.Profile;
import com.quizapp.profiles.ProfileSession;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class DashboardScreen extends StackPane {

    public DashboardScreen() {
        Profile profile = ProfileSession.getCurrentProfile();

        AnimatedBackground bg = new AnimatedBackground();

        VBox root = new VBox(28);
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.TOP_CENTER);

        HBox header = new HBox(30);
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("dashboard-hero");

        VBox titleBox = new VBox(8);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Analytics Dashboard");
        title.getStyleClass().add("dashboard-title");

        Label subtitle = new Label("Performance overview for " + profile.getName());
        subtitle.getStyleClass().add("dashboard-subtitle");

        titleBox.getChildren().addAll(title , subtitle);

        Label profileChip = new Label("ELO " + profile.getElo());
        profileChip.getStyleClass().add("dashboard-profile-chip");

        header.getChildren().addAll(titleBox , profileChip);

        FlowPane statsGrid = new FlowPane();
        statsGrid.setAlignment(Pos.CENTER);
        statsGrid.setHgap(18);
        statsGrid.setVgap(18);
        statsGrid.setMaxWidth(980);

        statsGrid.getChildren().addAll(
                        new StatCard("Games", String.valueOf(profile.getTotalGames())),
                        new StatCard("Accuracy", String.format("%.1f%%", profile.getOverallAccuracy())),
                        new StatCard("Questions", String.valueOf(profile.getTotalQuestions())),
                        new StatCard("Favorite Mode", profile.getFavoriteMode()));

        HBox visualSection = new HBox(24);
        visualSection.setAlignment(Pos.CENTER);

        PieChart chart = buildChart(profile);
        chart.getStyleClass().add("dashboard-pie");
        chart.setPrefSize(500 , 420);
        chart.setLabelsVisible(true);
        chart.setLegendVisible(true);

        visualSection.getChildren().add(chart);

        HBox actions = new HBox(16);
        actions.setAlignment(Pos.CENTER);

        Button webDashboard = new Button("Open Full Web Dashboard");
        webDashboard.getStyleClass().add("dashboard-big-button");
        webDashboard.setOnAction(e -> DashboardGenerator.openDashboard(profile));

        Button back = new Button("Back Home");
        back.getStyleClass().add("secondary-button");
        back.setOnAction(e -> NavigationManager.goTo(new HomeScreen()));

        actions.getChildren().addAll(webDashboard , back);

        root.getChildren().addAll(header , statsGrid , visualSection , actions);

        getChildren().addAll(bg , root);

        fadeIn(root);
    }

    private PieChart buildChart(Profile profile) {
        PieChart chart = new PieChart();

        int total = profile.getTimedGames()
                + profile.getSurvivalGames()
                + profile.getSuddenDeathGames()
                + profile.getPvpGames()
                + profile.getLlmGames()
                + profile.getEloGames();

        if (total == 0) {
            chart.getData().add(new PieChart.Data("No games yet" , 1));
            return chart;
        }

        addSlice(chart , "Timed" , profile.getTimedGames());
        addSlice(chart , "Survival" , profile.getSurvivalGames());
        addSlice(chart , "Sudden Death" , profile.getSuddenDeathGames());
        addSlice(chart , "PvP" , profile.getPvpGames());
        addSlice(chart , "LLM" , profile.getLlmGames());
        addSlice(chart , "ELO" , profile.getEloGames());

        return chart;
    }

    private void addSlice(PieChart chart , String name , int value) {
        if (value > 0) chart.getData().add(new PieChart.Data(name , value));
    }

    private void fadeIn(VBox root) {
        root.setOpacity(0);

        FadeTransition fade = new FadeTransition(Duration.millis(450) , root);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }
}