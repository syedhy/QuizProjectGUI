package com.quizapp.helpers;

import java.io.*;
import java.util.*;

public class ListMaker {

    public static List<Question> makeList(String filePath) {
        List<Question> questions = new ArrayList<>();

        try {
            BufferedReader br;

            File file = new File(filePath);

            if (file.exists()) {
                br = new BufferedReader(new FileReader(file));
            } else {
                InputStream stream = ListMaker.class.getClassLoader().getResourceAsStream(filePath);

                if (stream == null) {
                    System.err.println("Question file not found: " + filePath);
                    return questions;
                }

                br = new BufferedReader(new InputStreamReader(stream));
            }

            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("@");

                if (parts.length >= 6) {
                    questions.add(new Question(parts[0] , Arrays.copyOfRange(parts , 1 , 5) , parts[5]));
                }
            }

            br.close();

        } catch (Exception e) {
            System.err.println("Error reading questions: " + e.getMessage());
        }

        return questions;
    }
}