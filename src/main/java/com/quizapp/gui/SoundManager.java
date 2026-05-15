package com.quizapp.gui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundManager {

    private static MediaPlayer musicPlayer;

    public static void startMusic() {
        if (!AppSettings.isMusicEnabled()) {
            return;
        }

        if (musicPlayer != null) {
            musicPlayer.play();
            return;
        }

        try {
            String path =
                    SoundManager.class
                            .getResource("/audio/background.mp3")
                            .toExternalForm();

            Media media = new Media(path);

            musicPlayer = new MediaPlayer(media);
            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            musicPlayer.setVolume(0.18);
            musicPlayer.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.pause();
        }
    }

    public static void playCorrect() {
        playEffect("/audio/correct.mp3" , 0.35);
    }

    public static void playWrong() {
        playEffect("/audio/wrong.mp3" , 0.35);
    }

    private static void playEffect(String path , double volume) {
        if (!AppSettings.isMusicEnabled()) {
            return;
        }

        try {
            String resource =
                    SoundManager.class
                            .getResource(path)
                            .toExternalForm();

            Media media = new Media(resource);
            MediaPlayer player = new MediaPlayer(media);

            player.setVolume(volume);
            player.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}