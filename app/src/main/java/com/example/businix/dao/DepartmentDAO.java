package com.example.businix.dao;

import android.util.Log;

import com.example.businix.pojo.Department;
import com.example.businix.pojo.Employee;
import com.google.firebase.firestore.FirebaseFirestore;

public class DepartmentDAO {
    private FirebaseFirestore db;
    private String collectionPath;

    public DepartmentDAO() {
        db = FirebaseFirestore.getInstance();
        collectionPath = "departments";
    }

    public void addDepartment(Department department) {
        db.collection(collectionPath)
                .add(department)
                .addOnSuccessListener(documentReference -> {
                    Log.d("DepartmentHelper", "Thêm thành công với ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("DepartmentHelper", "Error adding document", e);
                });
    }
}
