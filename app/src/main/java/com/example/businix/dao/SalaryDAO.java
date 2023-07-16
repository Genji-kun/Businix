package com.example.businix.dao;

import com.google.firebase.firestore.FirebaseFirestore;

public class SalaryDAO {
    private FirebaseFirestore db;
    private String collectionPath;

    public SalaryDAO() {
        db = FirebaseFirestore.getInstance();
        collectionPath = "salaries";
    }
}
