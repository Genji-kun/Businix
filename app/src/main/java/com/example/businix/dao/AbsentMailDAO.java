package com.example.businix.dao;

import android.util.Log;

import com.example.businix.pojo.AbsentMail;
import com.example.businix.pojo.Department;
import com.google.firebase.firestore.FirebaseFirestore;

public class AbsentMailDAO {
    private FirebaseFirestore db;
    private String collectionPath;

    public AbsentMailDAO() {
        db = FirebaseFirestore.getInstance();
        collectionPath = "absentMails";
    }

    public void addAbsentMail(AbsentMail absentMail) {
        db.collection(collectionPath)
                .add(absentMail)
                .addOnSuccessListener(documentReference -> {
                    Log.d("AbsentMailHelper", "Thêm thành công với ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("AbentMailHelper", "Error adding document", e);
                });
    }
}
