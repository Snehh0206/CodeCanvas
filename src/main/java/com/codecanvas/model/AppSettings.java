package com.codecanvas.model;

public class AppSettings {

    private static final AppSettings instance = new AppSettings();

    private boolean darkMode = false;
    private boolean soundEnabled = true;
    private boolean animationEnabled = true;

    private AppSettings() {}

    public static AppSettings getInstance() { return instance; }

    public boolean isDarkMode() { return darkMode; }
    public void setDarkMode(boolean darkMode) { this.darkMode = darkMode; }

    public boolean isSoundEnabled() { return soundEnabled; }
    public void setSoundEnabled(boolean soundEnabled) { this.soundEnabled = soundEnabled; }

    public boolean isAnimationEnabled() { return animationEnabled; }
    public void setAnimationEnabled(boolean animationEnabled) { this.animationEnabled = animationEnabled; }
}