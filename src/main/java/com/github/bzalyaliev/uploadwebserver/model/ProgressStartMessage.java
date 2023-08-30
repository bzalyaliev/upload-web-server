package com.github.bzalyaliev.uploadwebserver.model;

public class ProgressStartMessage {
    private String username;
    private String fileName;

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "ProgressStartMessage{" +
                "username='" + username + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ProgressStartMessage(String username, String fileName) {
        this.username = username;
        this.fileName = fileName;
    }
}
