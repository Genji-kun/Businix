package com.example.businix.dao;

import android.util.Log;

import com.example.businix.models.AbsentMail;
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

public class AbsentMailDAO {
    private FirebaseFirestore db;
    private String collectionPath;

    public AbsentMailDAO() {
        db = FirebaseFirestore.getInstance();
        collectionPath = "absentMails";
    }

    public Task<Void> addAbsentMail(AbsentMail absentMail) {
        return db.collection(collectionPath)
                .add(absentMail).continueWith(task -> {
                    if (task.isSuccessful()) {
                        Log.d("AbsentMailDAO", "Thêm thành công với ID: " + task.getResult().getId());
                        return null;
                    } else {
                        Log.e("AbsentMailDAO", "Thêm không thành công", task.getException());
                        throw task.getException();
                    }
                });
    }

    public Task<Void> updateAbsentMail(String id, AbsentMail absenMail) {

        if (id.isEmpty() || absenMail == null) {
            Log.e("AbsentMailDAO", "absenMailId hoặc absenMail không hợp lệ");
            return Tasks.forException(new IllegalArgumentException("employeeId không hợp lệ"));
        }

        Map<String, Object> updates;
        try {
            updates = FirestoreUtils.prepareUpdates(absenMail);
        } catch (IllegalAccessException e) {
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

    public Task<List<AbsentMail>> getAbsentMailList() {
        CollectionReference absentMailRef = db.collection(collectionPath);
        return absentMailRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                List<AbsentMail> absentMailList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    AbsentMail absentMail = document.toObject(AbsentMail.class);
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

    public Task<AbsentMail> getAbsentMailById(String id) {
        DocumentReference absentMailRef = db.collection(collectionPath).document(id);
        return absentMailRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    return document.toObject(AbsentMail.class);
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
