package com.example.businix.dao;

import android.util.Log;

import com.example.businix.models.LeaveRequest;
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

public class LeaveRequestDAO {
    private FirebaseFirestore db;
    private String collectionPath;

    public LeaveRequestDAO() {
        db = FirebaseFirestore.getInstance();
        collectionPath = "absentMails";
    }

    public Task<Void> addAbsentMail(LeaveRequest absentMail) {
        return db.collection(collectionPath)
                .add(absentMail)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        Log.d("AbsentMailDAO", "Thêm thành công với ID: " + task.getResult().getId());
                        return null;
                    } else {
                        Log.e("AbsentMailDAO", "Thêm không thành công", task.getException());
                        throw task.getException();
                    }
                });
    }

    public Task<Void> updateAbsentMail(String id, LeaveRequest absenMail) {

        if (id.isEmpty() || absenMail == null) {
            Log.e("AbsentMailDAO", "absenMailId hoặc absenMail không hợp lệ");
            return Tasks.forException(new IllegalArgumentException("employeeId không hợp lệ"));
        }

        Map<String, Object> updates;
        try {
            updates = FirestoreUtils.prepareUpdates(absenMail);
        } catch (IllegalAccessException e) {
            Log.e("AbsentMailDAO", "Không lấy được dữ liệu updates.", e);
            return Tasks.forException(e);
        }

        return db.collection(collectionPath).document(id).update(updates).continueWith(task -> {
            if (task.isSuccessful()) {
                Log.d("AbsentMailDAO", "Cập nhật thành công absentMail có id: " + id);
                return null;
            }
            else {
                Log.e("AbsentMailDAO", "Cập nhật thất bại absentMail có id: " + id);
                throw task.getException();
            }
        });
    }

    public Task<List<LeaveRequest>> getAbsentMailList() {
        CollectionReference absentMailRef = db.collection(collectionPath);
        return absentMailRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                List<LeaveRequest> absentMailList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    LeaveRequest absentMail = document.toObject(LeaveRequest.class);
                    absentMail.setId(document.getId());
                    absentMailList.add(absentMail);
                }
                return absentMailList;
            }
            else {
                Log.e("AbsentMailDAO", "Lỗi khi lấy list absentMail", task.getException());
                throw task.getException();
            }
        });
    }

    public Task<LeaveRequest> getAbsentMailById(String id) {
        DocumentReference absentMailRef = db.collection(collectionPath).document(id);
        return absentMailRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    LeaveRequest absentMail = document.toObject(LeaveRequest.class);
                    absentMail.setId(document.getId());
                    return absentMail;
                } else {
                    throw new Exception("AbsentMail not found");
                }
            } else {
                Log.e("AbsentMailDAO", "Lỗi khi lấy absentMail với id " + id, task.getException());
                throw task.getException();
            }
        });
    }
}
