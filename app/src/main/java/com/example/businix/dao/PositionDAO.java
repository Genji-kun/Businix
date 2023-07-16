package com.example.businix.dao;

import android.util.Log;

import com.example.businix.pojo.Department;
import com.example.businix.pojo.Position;
import com.google.firebase.firestore.FirebaseFirestore;

public class PositionDAO {
    private FirebaseFirestore db;
    private String collectionPath;

    public PositionDAO() {
        db = FirebaseFirestore.getInstance();
        collectionPath = "positions";
    }

    public void addPosition(Position position) {
        db.collection(collectionPath)
                .add(position)
                .addOnSuccessListener(documentReference -> {
                    Log.d("PositionHelper", "Thêm thành công với ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("PositionHelper", "Error adding document", e);
                });
    }
}
