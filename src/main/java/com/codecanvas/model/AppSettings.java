package com.codecanvas.model;

public class AppSettings {

    private static final AppSettings instance = new AppSettings();

    private boolean darkMode = false;
    private boolean soundEnabled = true;
    private boolean animationEnabled = true;
    private String quizDifficulty = "medium";
    private int defaultSpeed = 5;

    private AppSettings() {}

    public static AppSettings getInstance() { return instance; }

    public boolean isDarkMode() { return darkMode; }
    public void setDarkMode(boolean darkMode) { this.darkMode = darkMode; }

    public boolean isSoundEnabled() { return soundEnabled; }
    public void setSoundEnabled(boolean soundEnabled) { this.soundEnabled = soundEnabled; }

    public boolean isAnimationEnabled() { return animationEnabled; }
    public void setAnimationEnabled(boolean animationEnabled) { this.animationEnabled = animationEnabled; }

    public String getQuizDifficulty() { return quizDifficulty; }
    public void setQuizDifficulty(String quizDifficulty) { this.quizDifficulty = quizDifficulty; }

    public int getDefaultSpeed() { return defaultSpeed; }
    public void setDefaultSpeed(int defaultSpeed) { this.defaultSpeed = defaultSpeed; }
}