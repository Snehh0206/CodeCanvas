package com.codecanvas.model;

import java.util.List;

public class QuizQuestion {
    private final String questionText;
    private final List<String> options;
    private final int correctOptionIndex;

    public QuizQuestion(String questionText, List<String> options, int correctOptionIndex) {
        this.questionText = questionText;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public int getCorrectOptionIndex() { return correctOptionIndex; }
}