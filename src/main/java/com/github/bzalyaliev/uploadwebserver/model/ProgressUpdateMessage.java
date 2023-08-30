package com.github.bzalyaliev.uploadwebserver.model;

public class ProgressUpdateMessage {
    private String username;
    private String videoName;
    private double progress;

    public ProgressUpdateMessage(String username, String videoName, double progress) {
        this.username = username;
        this.videoName = videoName;
        this.progress = progress;
    }

    public ProgressUpdateMessage(String username, double progress) {
        this.username = username;
        this.progress = progress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    // Конструктор, геттеры и сеттеры
}
