package com.codecanvas.service;

import com.codecanvas.model.AlgorithmStep;
import com.codecanvas.model.QuizQuestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuizGenerator {

    // Builds one "what happens next?" question from a step and the step right after it
    public static QuizQuestion buildQuestion(AlgorithmStep currentStep, AlgorithmStep nextStep) {
        List<String> options = new ArrayList<>();
        String correctAnswer;

        if (!nextStep.getSwappingIndices().isEmpty()) {
            correctAnswer = "A swap happens";
        } else if (!nextStep.getComparingIndices().isEmpty()) {
            correctAnswer = "Two elements get compared";
        } else {
            correctAnswer = "An element gets placed in its sorted position";
        }

        options.add("A swap happens");
        options.add("Two elements get compared");
        options.add("An element gets placed in its sorted position");
        options.add("Nothing changes");

        Collections.shuffle(options);
        int correctIndex = options.indexOf(correctAnswer);

        String questionText = "Current array: " + Arrays.toString(currentStep.getArrayState())
                + "\nWhat happens next?";

        return new QuizQuestion(questionText, options, correctIndex);
    }
}