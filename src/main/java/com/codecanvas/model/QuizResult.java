package com.codecanvas.model;

public class QuizResult {
    private int id;
    private String algorithm;
    private int score;
    private int totalQuestions;
    private String quizDate;

    public QuizResult(String algorithm, int score, int totalQuestions, String quizDate) {
        this.algorithm = algorithm;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.quizDate = quizDate;
    }

    public QuizResult(int id, String algorithm, int score, int totalQuestions, String quizDate) {
        this(algorithm, score, totalQuestions, quizDate);
        this.id = id;
    }

    public int getId() { return id; }
    public String getAlgorithm() { return algorithm; }
    public int getScore() { return score; }
    public int getTotalQuestions() { return totalQuestions; }
    public String getQuizDate() { return quizDate; }
}