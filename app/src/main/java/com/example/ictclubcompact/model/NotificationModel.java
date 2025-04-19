package com.example.ictclubcompact.model;

public class NotificationModel {
    private String id;
    private String title;
    private String message;
    private String link;
    private String date;

    // Required no-arg constructor for Firestore
    public NotificationModel() {}

    public NotificationModel(String id, String title, String message, String link, String date) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.link = link;
        this.date = date;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getLink() { return link; }
    public String getDate() { return date; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public void setLink(String link) { this.link = link; }
    public void setDate(String date) { this.date = date; }
}
