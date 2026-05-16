package com.quizapp.gui;

public class QuizModeConfig {

    private final String modeName;
    private final int questionLimit;
    private final int timeLimitSeconds;
    private final int lives;
    private final boolean suddenDeath;
    private final boolean ranked;

    public QuizModeConfig(
            String modeName,
            int questionLimit,
            int timeLimitSeconds,
            int lives,
            boolean suddenDeath,
            boolean ranked
    ) {
        this.modeName = modeName;
        this.questionLimit = questionLimit;
        this.timeLimitSeconds = timeLimitSeconds;
        this.lives = lives;
        this.suddenDeath = suddenDeath;
        this.ranked = ranked;
    }

    public String getModeName() {
        return modeName;
    }

    public int getQuestionLimit() {
        return questionLimit;
    }

    public int getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    public int getLives() {
        return lives;
    }

    public boolean hasTimer() {
        return timeLimitSeconds > 0;
    }

    public boolean hasLives() {
        return lives > 0;
    }

    public boolean isSuddenDeath() {
        return suddenDeath;
    }

    public boolean isRanked() {
        return ranked;
    }

    public static QuizModeConfig timed() {
        return new QuizModeConfig("Timed Mode", 10, 60, 0, false, false);
    }

    public static QuizModeConfig survival() {
        return new QuizModeConfig("Survival Mode", 20, 0, 3, false, false);
    }

    public static QuizModeConfig suddenDeath() {
        return new QuizModeConfig("Sudden Death", 50, 0, 1, true, false);
    }

    public static QuizModeConfig pvp() {
        return new QuizModeConfig("Player Vs Player", 10, 0, 0, false, false);
    }

    public static QuizModeConfig llm() {
        return new QuizModeConfig("LLM Mode", 10, 0, 0, false, false);
    }

    public static QuizModeConfig elo() {
        return new QuizModeConfig("ELO Mode", 5, 0, 0, false, true);
    }

    public static QuizModeConfig phoneQr() {
        return new QuizModeConfig("Phone QR Mode", 10, 0, 0, false, false);
    }
}