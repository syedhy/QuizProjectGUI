package com.quizapp.modes.llmmode.helpers;

import java.util.ArrayList;
import java.util.List;

import com.quizapp.helpers.Question;

public class QuizParser {

    public static List<Question> parseQuestions(String raw) {
        List<Question> questions = new ArrayList<>();

        if (raw == null || raw.isBlank()) {
            return questions;
        }

        String[] lines = raw.split("\\R");

        for (String line : lines) {
            line = cleanLine(line);

            if (line.isBlank() || !line.contains("@")) {
                continue;
            }

            String[] parts = line.split("@");

            if (parts.length < 6) {
                continue;
            }

            String questionText = parts[0].trim();
            String optionA = parts[1].trim();
            String optionB = parts[2].trim();
            String optionC = parts[3].trim();
            String optionD = parts[4].trim();
            String answer = parts[5].trim().toUpperCase();

            if (questionText.isBlank()
                    || optionA.isBlank()
                    || optionB.isBlank()
                    || optionC.isBlank()
                    || optionD.isBlank()
                    || !answer.matches("[ABCD]")) {
                continue;
            }

            questions.add(
                    new Question(
                            questionText,
                            new String[]{optionA, optionB, optionC, optionD},
                            answer
                    )
            );

            if (questions.size() == 10) {
                break;
            }
        }

        return questions;
    }

    public static List<String> extractValidQuestions(String raw) {
        List<String> validQuestions = new ArrayList<>();

        for (Question question : parseQuestions(raw)) {
            validQuestions.add(
                    question.question
                            + " @ " + question.options[0]
                            + " @ " + question.options[1]
                            + " @ " + question.options[2]
                            + " @ " + question.options[3]
                            + " @ " + question.answer
            );
        }

        return validQuestions;
    }

    private static String cleanLine(String line) {
        if (line == null) {
            return "";
        }

        return line
                .replace("`", "")
                .replace("*", "")
                .replace("•", "")
                .replaceFirst("^\\d+\\.\\s*", "")
                .trim();
    }
}