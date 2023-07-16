package com.example.businix.dao;

import android.util.Log;

import com.example.businix.pojo.Employee;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private FirebaseFirestore db;
    private String collectionPath;

    public EmployeeDAO() {
        db = FirebaseFirestore.getInstance();
        collectionPath = "employees";
    }

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

    public void deleteEmployee(String id) {
        db.collection(collectionPath).document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("EmployeeHelper", "Xóa thành công");
                })
                .addOnFailureListener(e -> {
                    Log.w("EmployeeHelper", "Error deleting document", e);
                });
    }

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


    public Task<Employee> getEmployeeById(String employeeId) {
        DocumentReference employeeRef = db.collection("employees").document(employeeId);
        return employeeRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    return document.toObject(Employee.class);
                } else {
                    throw new Exception("Employee not found");
                }
            } else {
                throw task.getException();
            }
        });
    }


}
