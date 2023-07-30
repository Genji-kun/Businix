package com.example.businix.dao;

import android.util.Log;

import com.example.businix.models.Attendance;
import com.example.businix.models.Employee;
import com.example.businix.utils.DateUtils;
import com.example.businix.utils.FirestoreUtils;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AttendanceDAO {
    private final FirebaseFirestore db;
    private final String collectionPath;

    public AttendanceDAO() {
        db = FirebaseFirestore.getInstance();
        collectionPath = "attendances";
    }

    public Task<Void> addAttendance(Attendance attendance) {
        return db.collection(collectionPath)
                .add(attendance)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        Log.d("AttendanceDAO", "Thêm thành công với ID: " + task.getResult().getId());
                        return null;
                    } else {
                        Log.w("AttendanceDAO", "Error adding document", task.getException());
                        throw task.getException();
                    }
                });
    }

    public Task<Attendance> getAttendanceById(String id) {
        DocumentReference attendanceRef = db.collection(collectionPath).document(id);
        return attendanceRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Attendance attendance = document.toObject(Attendance.class);
                    attendance.setId(document.getId());
                    return attendance;
                } else {
                    return null;
                }
            } else {
                throw task.getException();
            }
        });
    }

    public Task<Void> updateAttandance(String id, Attendance attendance) {
        if (attendance == null || id.isEmpty()) {
            Log.e("AttendanceDAO", "id không hợp lệ");
            return Tasks.forException(new IllegalArgumentException("id không hợp lệ"));
        }
        Map<String, Object> updates;
        try {
            updates = FirestoreUtils.prepareUpdates(attendance);
        } catch (IllegalAccessException e) {
            Log.e("AttendanceDAO", "Không lấy được dữ liệu updates.", e);
            return Tasks.forException(e);
        }

        DocumentReference documentRef = db.collection(collectionPath).document(id);

        return documentRef.update(updates)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        Log.d("AttendanceDAO", "Attendance cập nhật thành công.");
                        return null;
                    } else {
                        Log.e("AttendanceDAO", "Attendance cập nhật thất bại.", task.getException());
                        throw task.getException();
                    }
                });
    }

    public Task<Attendance> getAttandanceByDate(Date minTime, Date maxTime, DocumentReference emp) {
        Query query = db.collection(collectionPath);
        if (minTime != null && maxTime != null) {
            query = query.whereGreaterThanOrEqualTo("checkInTime", minTime).whereLessThanOrEqualTo("checkInTime", maxTime)
                    .whereEqualTo("employee", emp);
        } else {
            return null;
        }
        return query.limit(1).get().continueWith(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().isEmpty()) {
                    DocumentSnapshot attendRef = task.getResult().getDocuments().get(0);
                    Attendance attendance = attendRef.toObject(Attendance.class);
                    attendance.setId(attendRef.getId());
                    Log.d("AttendanceDAO", "lấy Attendance thành công.");
                    return attendance;
                } else {
                    return null;
                }
            } else {
                Log.e("AttendanceDAO", "Lỗi khi truy xuất", task.getException());
                throw task.getException();
            }
        });
    }
}
