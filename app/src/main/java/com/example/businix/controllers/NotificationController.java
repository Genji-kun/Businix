package com.example.businix.controllers;

import com.example.businix.dao.NotificationDAO;
import com.example.businix.models.Notification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class NotificationController {
    private NotificationDAO notificationDao;

    public NotificationController() {
        notificationDao = new NotificationDAO();
    }

    public void addNotification(Notification notification, OnCompleteListener<String> onCompleteListener) {
        Task<String> addNotificationTask = notificationDao.addNotification(notification);
        addNotificationTask.addOnCompleteListener(onCompleteListener);
    }

    public void getNotifications(String id, OnCompleteListener<List<Notification>> onCompleteListener) {
        Task<List<Notification>> getNotificationsTask = notificationDao.getNotifications(id);
        getNotificationsTask.addOnCompleteListener(onCompleteListener);
    }

    public void updateNotification(String id, Notification notification , OnCompleteListener<Void> onCompleteListener) {
        Task<Void> addNotificationTask = notificationDao.updateNotification(id,notification);
        addNotificationTask.addOnCompleteListener(onCompleteListener);
    }
}
