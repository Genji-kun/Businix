package com.example.businix.models;

import com.google.firebase.firestore.Exclude;

public class Department {
    @Exclude
    private String id;
    private String name;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }
}
