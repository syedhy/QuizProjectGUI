package com.quizapp.gui;

public class AppSettings {

    private static boolean animationsEnabled = true;
    private static boolean musicEnabled = true;

    public static boolean isAnimationsEnabled() {
        return animationsEnabled;
    }

    public static void setAnimationsEnabled(boolean value) {
        animationsEnabled = value;
    }

    public static boolean isMusicEnabled() {
        return musicEnabled;
    }

    public static void setMusicEnabled(boolean value) {
        musicEnabled = value;
    }
}