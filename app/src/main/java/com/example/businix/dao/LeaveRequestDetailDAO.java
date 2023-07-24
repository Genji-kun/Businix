package com.example.businix.dao;

import android.util.Log;

import com.example.businix.models.LeaveRequest;
import com.example.businix.models.LeaveRequestDetail;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class LeaveRequestDetailDAO {
    private FirebaseFirestore db;
    private String collectionPath;

    public LeaveRequestDetailDAO() {
        db = FirebaseFirestore.getInstance();
        collectionPath = "leaveRequestDetails";
    }

    public Task<Void> addLeaveRequestDetail(LeaveRequestDetail leaveRequestDetail) {
        return db.collection(collectionPath)
                .add(leaveRequestDetail)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        Log.d("LeaveRequestDetailDAO", "Thêm thành công với ID: " + task.getResult().getId());
                        return null;
                    } else {
                        Log.e("LeaveRequestDetailDAO", "Thêm không thành công", task.getException());
                        throw task.getException();
                    }
                });
    }

    public Task<Void> addLeaveRequestDetails(List<LeaveRequestDetail> leaveRequestDetails) {
        return db.runTransaction(transaction -> {
            for (LeaveRequestDetail leaveRequestDetail : leaveRequestDetails) {
                DocumentReference newDocRef = db.collection(collectionPath).document();
                transaction.set(newDocRef, leaveRequestDetail);
            }
            return null;
        });
    }

    public Task<List<LeaveRequestDetail>> getLeaveRequestDetailList(DocumentReference leaveRequest) {
        return db.collection(collectionPath)
                .whereEqualTo("leaveRequest", leaveRequest)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        List<LeaveRequestDetail> detailList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            LeaveRequestDetail detail = document.toObject(LeaveRequestDetail.class);
                            detail.setId(document.getId());
                            detailList.add(detail);
                        }
                        return detailList;
                    } else {
                        Log.e("LeaveRequestDetailDAO", "Lỗi khi lấy list LeaveRequestDetail", task.getException());
                        throw task.getException();
                    }
                });
    }


}