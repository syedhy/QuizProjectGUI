package com.quizapp.network;

import java.util.List;

import com.quizapp.helpers.Question;

public class PhoneQuizState {

    private final List<Question> questions;

    private int currentIndex;
    private int correctAnswers;
    private int wrongAnswers;
    private boolean finished;

    private boolean hasLastAnswer;
    private boolean lastAnswerCorrect;
    private int answerVersion;

    public PhoneQuizState(List<Question> questions) {
        this.questions = questions;
        this.currentIndex = 0;
        this.correctAnswers = 0;
        this.wrongAnswers = 0;
        this.finished = false;
        this.hasLastAnswer = false;
        this.lastAnswerCorrect = false;
        this.answerVersion = 0;
    }

    public synchronized Question getCurrentQuestion() {
        if (finished || questions == null || questions.isEmpty()) {
            return null;
        }

        return questions.get(currentIndex);
    }

    public synchronized void submitAnswer(String selectedAnswer) {
        if (finished) {
            return;
        }

        Question question = getCurrentQuestion();

        if (question == null) {
            finished = true;
            return;
        }

        String correct = question.answer.trim().toUpperCase();
        String selected = selectedAnswer.trim().toUpperCase();

        lastAnswerCorrect = correct.equals(selected);
        hasLastAnswer = true;
        answerVersion++;

        if (lastAnswerCorrect) {
            correctAnswers++;
        } else {
            wrongAnswers++;
        }

        if (currentIndex >= questions.size() - 1) {
            finished = true;
        } else {
            currentIndex++;
        }
    }

    public synchronized boolean hasLastAnswer() {
        return hasLastAnswer;
    }

    public synchronized boolean wasLastAnswerCorrect() {
        return lastAnswerCorrect;
    }

    public synchronized int getAnswerVersion() {
        return answerVersion;
    }

    public synchronized int getCurrentIndex() {
        return currentIndex;
    }

    public synchronized int getTotalQuestions() {
        return questions == null ? 0 : questions.size();
    }

    public synchronized int getCorrectAnswers() {
        return correctAnswers;
    }

    public synchronized int getWrongAnswers() {
        return wrongAnswers;
    }

    public synchronized boolean isFinished() {
        return finished;
    }
}