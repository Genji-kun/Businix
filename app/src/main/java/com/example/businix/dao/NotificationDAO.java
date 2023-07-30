package com.example.businix.dao;

import android.util.Log;

import com.example.businix.models.LeaveRequest;
import com.example.businix.models.Notification;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
}
