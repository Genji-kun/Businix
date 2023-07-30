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
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class LeaveRequestDAO {
    private FirebaseFirestore db;
    private String collectionPath;

    public LeaveRequestDAO() {
        db = FirebaseFirestore.getInstance();
        collectionPath = "leaveRequests";
    }

    public Task<String> addLeaveRequest(LeaveRequest leaveRequest) {
        leaveRequest.setCreatedAt(Calendar.getInstance().getTime());
        return db.collection(collectionPath)
                .add(leaveRequest)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        Log.d("LeaveRequestDAO", "Thêm thành công với ID: " + task.getResult().getId());
                        return task.getResult().getId();
                    } else {
                        Log.e("LeaveRequestDAO", "Thêm không thành công", task.getException());
                        throw task.getException();
                    }
                });
    }

    public Task<Void> updateLeaveRequest(String id, LeaveRequest absenMail) {

        if (id.isEmpty() || absenMail == null) {
            Log.e("LeaveRequestDAO", "absenMailId hoặc absenMail không hợp lệ");
            return Tasks.forException(new IllegalArgumentException("employeeId không hợp lệ"));
        }

        Map<String, Object> updates;
        try {
            updates = FirestoreUtils.prepareUpdates(absenMail);
        } catch (IllegalAccessException e) {
            Log.e("LeaveRequestDAO", "Không lấy được dữ liệu updates.", e);
            return Tasks.forException(e);
        }

        return db.collection(collectionPath).document(id).update(updates).continueWith(task -> {
            if (task.isSuccessful()) {
                Log.d("LeaveRequestDAO", "Cập nhật thành công leaveRequest có id: " + id);
                return null;
            } else {
                Log.e("LeaveRequestDAO", "Cập nhật thất bại leaveRequest có id: " + id);
                throw task.getException();
            }
        });
    }

    public Task<List<LeaveRequest>> getLeaveRequestList() {
        CollectionReference leaveRequestRef = db.collection(collectionPath);
        return leaveRequestRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                List<LeaveRequest> leaveRequestList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    LeaveRequest leaveRequest = document.toObject(LeaveRequest.class);
                    leaveRequest.setId(document.getId());
                    leaveRequestList.add(leaveRequest);
                }
                return leaveRequestList;
            } else {
                Log.e("LeaveRequestDAO", "Lỗi khi lấy list LeaveRequest", task.getException());
                throw task.getException();
            }
        });
    }

    public Task<LeaveRequest> getLeaveRequestById(String id) {
        DocumentReference leaveRequestRef = db.collection(collectionPath).document(id);
        return leaveRequestRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    LeaveRequest leaveRequest = document.toObject(LeaveRequest.class);
                    leaveRequest.setId(document.getId());
                    return leaveRequest;
                } else {
                    throw new Exception("LeaveRequest not found");
                }
            } else {
                Log.e("LeaveRequestDAO", "Lỗi khi lấy leaveRequest với id " + id, task.getException());
                throw task.getException();
            }
        });
    }

    public DocumentReference getLeaveRequestRef(String id) {
        return db.collection(collectionPath).document(id);
    }
}
