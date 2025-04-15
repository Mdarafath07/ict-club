package com.example.ictclubcompact;

public class NotificationModel {
    private String title;
    private String message;
    private String date;
    private String link;

    // Default constructor (for Firestore)
    public NotificationModel() {
        // Needed for Firestore
    }

    public NotificationModel(String title, String message, String date, String link) {
        this.title = title;
        this.message = message;
        this.date = date;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public String getLink() {
        return link;
    }
}
