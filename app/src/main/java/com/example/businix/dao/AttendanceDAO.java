package com.example.businix.dao;

import android.util.Log;

import com.example.businix.models.Attendance;
import com.example.businix.models.Employee;
import com.example.businix.utils.DateUtils;
import com.example.businix.utils.FirestoreUtils;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.Date;
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

    public Task<Attendance> getAttandanceByDate(Date minTime, Date maxTime) {
        Query query = db.collection(collectionPath);
        if (minTime != null) {
            query = query.whereGreaterThanOrEqualTo("checkInTime", minTime);
        }
        if (maxTime != null) {
            query = query.whereLessThanOrEqualTo("checkOutTime", maxTime);
        }
        return query.limit(1).get().continueWith(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().isEmpty()) {
                    DocumentSnapshot attendRef = task.getResult().getDocuments().get(0);
                    Attendance attendance = attendRef.toObject(Attendance.class);
                    attendance.setId(attendRef.getId());
                    Log.d("AttendanceDAO", "lấy Attendance thành công.");
                    return attendance;
                }
                else {
                    return null;
                }
            }
            else {
                throw task.getException();
            }
        });
    }
}
