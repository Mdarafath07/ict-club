package com.example.ictclubcompact.model;


public class ScheduleModel {
    private String id;
    private String imageUrl;
    private String subject;
    private String date;
    private String time;
    private String mentor;
    private String notes;

    public ScheduleModel() {
        // Required for Firebase
    }

    public ScheduleModel(String imageUrl, String subject, String date, String time, String mentor, String notes) {
        this.imageUrl = imageUrl;
        this.subject = subject;
        this.date = date;
        this.time = time;
        this.mentor = mentor;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMentor() {
        return mentor;
    }

    public void setMentor(String mentor) {
        this.mentor = mentor;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
