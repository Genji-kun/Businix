package com.example.businix.repository.impl;

import android.util.Log;

import com.example.businix.pojo.Employee;
import com.example.businix.repository.EmployeeRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EmployeeRepositoryImpl implements EmployeeRepository {
    private FirebaseFirestore db;
    private String collectionPath;

    public EmployeeRepositoryImpl() {
        db = FirebaseFirestore.getInstance();
        collectionPath = "employees";
    }

    @Override
    public void addEmployee(Employee employee) {
        db.collection(collectionPath)
                .add(employee)
                .addOnSuccessListener(documentReference -> {
                    Log.d("EmployeeHelper", "Thêm thành công với ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("EmployeeHelper", "Error adding document", e);
                });
    }

    @Override
    public void updateEmployee(String id, Employee employee) {
        db.collection(collectionPath).document(id)
                .set(employee)
                .addOnSuccessListener(command -> {
                    Log.d("EmployeeHelper", "Cập nhật thành công");
                })
                .addOnFailureListener(e -> {
                    Log.w("EmployeeHelper", "Error updating document", e);
                });
    }

    @Override
    public void deleteEmployee(String id) {
        db.collection(collectionPath).document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("EmployeeHelper", "Xóa thành công");
                })
                .addOnFailureListener(e -> {
                    Log.w("EmployeeHelper", "Error deleting document", e);
                });
    }

    @Override
    public Task<List<Employee>> getEmployeeList() {
        TaskCompletionSource<List<Employee>> taskCompletionSource = new TaskCompletionSource<>();
        List<Employee> employeeList = new ArrayList<>();
        db.collection(collectionPath)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Employee employee = document.toObject(Employee.class);
                            employeeList.add(employee);
                        }
                        taskCompletionSource.setResult(employeeList);
                    } else {
                        Log.w("EmployeeHelper", "Error getting documents.", task.getException());
                        taskCompletionSource.setException(task.getException());
                    }
                });
        return taskCompletionSource.getTask();
    }


}
