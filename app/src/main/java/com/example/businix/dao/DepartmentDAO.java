package com.example.businix.dao;

import android.util.Log;

import com.example.businix.models.Department;
import com.example.businix.models.Position;
import com.example.businix.utils.FirestoreUtils;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DepartmentDAO {
    private final FirebaseFirestore db;
    private final String collectionPath;

    public DepartmentDAO() {
        db = FirebaseFirestore.getInstance();
        collectionPath = "departments";
    }

    public Task<Void> addDepartment(Department department) {
        return db.collection(collectionPath)
                .add(department)
                .continueWith(task -> {
                    if(task.isSuccessful()) {
                        Log.d("DepartmentDAO", "Thêm thành công với ID: " + task.getResult().getId());
                        return null;
                    }
                    else {
                        Log.w("DepartmentDAO", "Error adding document", task.getException());
                        throw task.getException();
                    }
                });
    }
    

    public Task<Void> updateDepartment(String id, Department department) {

        if (department == null || id.isEmpty()) {
            Log.e("DepartmentDAO", "departmentId không hợp lệ");
            return Tasks.forException(new IllegalArgumentException("employeeId không hợp lệ"));
        }

        Map<String, Object> updates;
        try {
            updates = FirestoreUtils.prepareUpdates(department);
        } catch (IllegalAccessException e) {
            Log.e("DepartmentDAO", "Không lấy được dữ liệu updates.", e);
            return Tasks.forException(e);
        }

        DocumentReference documentRef = db.collection(collectionPath).document(id);
        return documentRef.update(updates)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        Log.d("DepartmentDAO", "Department cập nhật thành công.");
                        return null; // Trả về null để biểu thị việc thành công
                    } else {
                        Log.e("DepartmentDAO", "Department cập nhật thất bại.", task.getException());
                        throw task.getException();
                    }
                });
    }

    public Task<Void> deleteDepartment(String id) {
        return db.collection(collectionPath).document(id).delete()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        Log.d("DepartmentDAO", "Xóa thành công");
                        return null;
                    } else {
                        Log.e("DepartmentDAO", "Error deleting document", task.getException());
                        throw task.getException();
                    }
                });
    }

    public Task<List<Department>> getDepartmentList() {
        CollectionReference departmentsRef = db.collection(collectionPath);
        return departmentsRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                List<Department> departmentList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Department department = document.toObject(Department.class);
                    department.setId(document.getId());
                    departmentList.add(department);
                }
                return departmentList;
            }
            else {
                Log.e("DepartmentDAO", "Lỗi khi lấy list department", task.getException());
                throw task.getException();
            }
        });
    }

    public Task<Department> getDepartmentById(String departmentId) {
        DocumentReference departmentRef = db.collection(collectionPath).document(departmentId);
        return departmentRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Department department = document.toObject(Department.class);
                    department.setId(document.getId());
                    return department;
                } else {
                    throw new Exception("Department not found");
                }
            } else {
                Log.e("DepartmentDAO", "Lỗi khi lấy department với id " + departmentId, task.getException());
                throw task.getException();
            }
        });
    }

    public Task<Department> getDepartmentByName(String departmentName) {
        return db.collection("departments")
                .whereEqualTo("name", departmentName)
                .limit(1)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            if (document.exists()) {
                                Department department = document.toObject(Department.class);
                                department.setId(document.getId());
                                return department;
                            } else {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    } else {
                        throw task.getException();
                    }
                });
    }
}
