package com.quizapp.gui;

public class QuizResultData {

    private final int score;
    private final int totalQuestions;
    private final int correctAnswers;
    private final int wrongAnswers;
    private final int oldElo;
    private final int newElo;

    public QuizResultData(int score , int totalQuestions , int correctAnswers , int wrongAnswers) {
        this(score , totalQuestions , correctAnswers , wrongAnswers , -1 , -1);
    }

    public QuizResultData(int score , int totalQuestions , int correctAnswers , int wrongAnswers , int oldElo , int newElo) {
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.oldElo = oldElo;
        this.newElo = newElo;
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public int getOldElo() {
        return oldElo;
    }

    public int getNewElo() {
        return newElo;
    }

    public int getEloChange() {
        if (!hasEloChange()) return 0;

        return newElo - oldElo;
    }

    public boolean hasEloChange() {
        return oldElo >= 0 && newElo >= 0;
    }

    public double getAccuracy() {
        if (totalQuestions == 0) return 0;

        return ((double) correctAnswers / totalQuestions) * 100.0;
    }
}