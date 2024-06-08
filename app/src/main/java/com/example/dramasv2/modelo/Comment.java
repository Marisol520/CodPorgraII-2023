package com.example.dramasv2.modelo;

public class Comment {
    private String user;
    private String text;
    private String time;

    public Comment() {
        // Constructor vacío necesario para Firebase
    }

    public Comment(String user, String text, String time) {
        this.user = user;
        this.text = text;
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
