package com.codecanvas.model;

public class QuizResult {
    private int id;
    private Integer runId; // nullable — null if standalone quiz, not linked to a run
    private String algorithm;
    private int score;
    private int totalQuestions;
    private String quizDate;

    public QuizResult(Integer runId, String algorithm, int score, int totalQuestions, String quizDate) {
        this.runId = runId;
        this.algorithm = algorithm;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.quizDate = quizDate;
    }

    public QuizResult(int id, Integer runId, String algorithm, int score, int totalQuestions, String quizDate) {
        this(runId, algorithm, score, totalQuestions, quizDate);
        this.id = id;
    }

    public int getId() { return id; }
    public Integer getRunId() { return runId; }
    public String getAlgorithm() { return algorithm; }
    public int getScore() { return score; }
    public int getTotalQuestions() { return totalQuestions; }
    public String getQuizDate() { return quizDate; }
}