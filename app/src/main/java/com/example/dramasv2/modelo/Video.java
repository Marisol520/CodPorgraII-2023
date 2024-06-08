package com.example.dramasv2.modelo;

public class Video {
    private String title;
    private int chapterNumber;
    private String videoUrl;

    // Constructor vacío requerido para Firebase
    public Video() {
    }

    public Video(String title, int chapterNumber, String videoUrl) {
        this.title = title;
        this.chapterNumber = chapterNumber;
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}

