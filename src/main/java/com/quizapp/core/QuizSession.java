package com.quizapp.core;

import java.util.List;

import com.quizapp.helpers.Question;

public class QuizSession {

    private final List<Question> questions;

    private int currentIndex;
    private int correctAnswers;

    public QuizSession(List<Question> questions) {
        this.questions = questions;
    }

    public Question getCurrentQuestion() {
        return questions.get(currentIndex);
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getTotalQuestions() {
        return questions.size();
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public boolean hasNextQuestion() {
        return currentIndex < questions.size() - 1;
    }

    public void nextQuestion() {
        currentIndex++;
    }

    public void submitAnswer(String selectedLetter) {
        Question q = getCurrentQuestion();

        if (q.answer.trim().equalsIgnoreCase(selectedLetter.trim())) {
            correctAnswers++;
        }
    }
}