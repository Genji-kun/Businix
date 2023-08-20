package com.example.businix.models;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.util.Date;
import java.util.List;

public class Notification {
    @Exclude
    private String id;
    private String title;
    private String message;
    private DocumentReference sender;
    private List<DocumentReference> receivers;
    private Boolean read;
    private Date createdAt;

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DocumentReference getSender() {
        return sender;
    }

    public void setSender(DocumentReference sender) {
        this.sender = sender;
    }

    public List<DocumentReference> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<DocumentReference> receivers) {
        this.receivers = receivers;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
