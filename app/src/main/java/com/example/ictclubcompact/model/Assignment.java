package com.example.ictclubcompact.model;
import com.example.ictclubcompact.model.Assignment;



public class Assignment {
    private String id;
    private String title;
    private String dueDate;

    public Assignment(String id, String title, String dueDate) {
        this.id = id;
        this.title = title;
        this.dueDate = dueDate;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDueDate() { return dueDate; }
}