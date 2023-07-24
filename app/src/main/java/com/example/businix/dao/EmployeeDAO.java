package com.example.businix.dao;

import android.util.Log;

import com.example.businix.models.Employee;
import com.example.businix.utils.FirestoreUtils;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class EmployeeDAO {
    private FirebaseFirestore db;
    private String collectionPath;

    public EmployeeDAO() {
        db = FirebaseFirestore.getInstance();
        collectionPath = "employees";
    }


    public Task<Void> addEmployee(Employee employee) {
        employee.setCreateAt(Calendar.getInstance().getTime());
        return db.collection(collectionPath)
                .add(employee)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        Log.d("EmployeeDAO", "Thêm thành công với ID: " + task.getResult().getId());
                        return null;
                    }
                    else {
                        Log.w("EmployeeDAO", "Error adding document", task.getException());
                        throw task.getException();
                    }
                });
    }

    public Task<Void> updateEmployee(String employeeId, Employee employee) {
        // Kiểm tra xem EmployeeId có hợp lệ hay không
        if (employee == null || employeeId.isEmpty()) {
            Log.e("EmployeeDAO", "employeeId không hợp lệ");
            return Tasks.forException(new IllegalArgumentException("employeeId không hợp lệ"));
        }

        // Chuẩn bị dữ liệu cập nhật từ đối tượng Employee
        Map<String, Object> updates;
        try {
            updates = FirestoreUtils.prepareUpdates(employee);
        } catch (IllegalAccessException e) {
            Log.e("EmployeeDAO", "Không lấy được dữ liệu updates.", e);
            return Tasks.forException(e);
        }

        DocumentReference documentRef = db.collection(collectionPath).document(employeeId);

        // Thực hiện cập nhật dữ liệu vào Firestore
        return documentRef.update(updates)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        Log.d("EmployeeDAO", "Employee cập nhật thành công.");
                        return null; // Trả về null để biểu thị việc thành công
                    } else {
                        Log.e("EmployeeDAO", "Employee cập nhật thất bại.", task.getException());
                        throw task.getException(); // Ném ngoại lệ để biểu thị việc thất bại
                    }
                });
    }


    public Task<Void> deleteEmployee(String id) {
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        // Kiểm tra xem ID nhân viên có hợp lệ hay không
        if (id == null || id.isEmpty()) {
            Log.e("EmployeeDAO", "Invalid employee ID.");
            taskCompletionSource.setException(new IllegalArgumentException("Invalid employee ID"));
            return taskCompletionSource.getTask();
        }

        // Thực hiện xóa nhân viên
        db.collection(collectionPath).document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("EmployeeDAO", "Xóa thành công nhân viên với ID: " + id);
                    taskCompletionSource.setResult(null);
                })
                .addOnFailureListener(e -> {
                    Log.w("EmployeeDAO", "Error deleting document", e);
                    taskCompletionSource.setException(e);
                });

        return taskCompletionSource.getTask();
    }

    public Task<List<Employee>> getEmployeeList() {
        CollectionReference employeesRef = db.collection(collectionPath);
        return employeesRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                List<Employee> employeeList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Employee employee = document.toObject(Employee.class);
                    employee.setId(document.getId());
                    employeeList.add(employee);
                }
                return employeeList;
            } else {
                throw task.getException();
            }
        });
    }


    public Task<Employee> getEmployeeById(String employeeId) {
        DocumentReference employeeRef = db.collection("employees").document(employeeId);
        return employeeRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Employee employee = document.toObject(Employee.class);
                    employee.setId(document.getId());
                    return employee;
                } else {
                    throw new Exception("Employee not found");
                }
            } else {
                throw task.getException();
            }
        });
    }

    public Task<Employee> getEmployeeByUsername(String username) {
        // Tạo một truy vấn để lấy nhân viên có username cần tìm
        return db.collection(collectionPath)
                .whereEqualTo("username", username)
                .limit(1)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot empRef = task.getResult().getDocuments().get(0);
                        Employee employee = empRef.toObject(Employee.class);
                        employee.setId(empRef.getId());
                        return employee;
                    } else {
                        throw new Exception("Employee not found");
                    }
                });
    }

    public DocumentReference getEmployeeRef(String id) {
        return db.collection(collectionPath).document(id);
    }

}
