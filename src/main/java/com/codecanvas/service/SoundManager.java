package com.codecanvas.service;

import javafx.scene.media.AudioClip;

public class SoundManager {

    private static boolean enabled = true;

    private static final AudioClip compareSound = loadSound("compare.wav");
    private static final AudioClip swapSound = loadSound("swap.wav");

    private static AudioClip loadSound(String fileName) {
        String path = SoundManager.class.getResource("/com/codecanvas/sounds/" + fileName).toExternalForm();
        return new AudioClip(path);
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    public static void playCompare() {
        if (enabled) compareSound.play();
    }

    public static void playSwap() {
        if (enabled) swapSound.play();
    }
}