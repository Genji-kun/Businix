package com.example.businix.dao;

import android.util.Log;

import com.example.businix.models.Department;
import com.example.businix.models.LeaveRequest;
import com.example.businix.models.Notification;
import com.example.businix.utils.FirestoreUtils;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class NotificationDAO {
    private FirebaseFirestore db;
    private String collectionPath;

    public NotificationDAO() {
        db = FirebaseFirestore.getInstance();
        collectionPath = "notifications";
    }

    public Task<String> addNotification(Notification notification) {
        notification.setCreatedAt(Calendar.getInstance().getTime());
        return db.collection(collectionPath)
                .add(notification)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        Log.d("NotificationDAO", "Thêm thành công với ID: " + task.getResult().getId());
                        return task.getResult().getId();
                    } else {
                        Log.e("NotificationDAO", "Thêm không thành công", task.getException());
                        throw task.getException();
                    }
                });
    }

    public Task<List<Notification>> getNotifications (String employeeId) {
        CollectionReference notificationRef = db.collection(collectionPath);

        Query query = notificationRef.whereArrayContains("receivers", db.collection("employees").document(employeeId));

        return query.get().continueWith(task -> {
            if (task.isSuccessful()) {
                List<Notification> notificationList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Notification notification = document.toObject(Notification.class);
                    notification.setId(document.getId());
                    notificationList.add(notification);
                }
                return notificationList;
            } else {
                throw task.getException();
            }
        });
    }

    public Task<Void> updateNotification(String id, Notification notification) {

        if (notification == null || id.isEmpty()) {
            Log.e("NotificationDAO", "notiId không hợp lệ");
            return Tasks.forException(new IllegalArgumentException("employeeId không hợp lệ"));
        }

        Map<String, Object> updates;
        try {
            updates = FirestoreUtils.prepareUpdates(notification);
        } catch (IllegalAccessException e) {
            Log.e("NotificationDAO", "Không lấy được dữ liệu updates.", e);
            return Tasks.forException(e);
        }

        DocumentReference documentRef = db.collection(collectionPath).document(id);
        return documentRef.update(updates)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        Log.d("NotificationDAO", "Notification cập nhật thành công.");
                        return null; // Trả về null để biểu thị việc thành công
                    } else {
                        Log.e("NotificationDAO", "Notification cập nhật thất bại.", task.getException());
                        throw task.getException();
                    }
                });
    }

}
